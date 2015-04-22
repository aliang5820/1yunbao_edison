/*
 * Copyright (C) 2013 Manuel Peinado
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maogousoft.logisticsmobile.driver.adapter;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.maogousoft.logisticsmobile.driver.R;

class MultiChoiceAdapterHelper implements OnItemLongClickListener, OnItemClickListener, OnCheckedChangeListener {

	private static final String BUNDLE_KEY = "mca__selection";

	/**
	 * Changes the selection state of the clicked item, just as if it had been
	 * long clicked. This is what the native MULTICHOICE_MODAL mode of List does,
	 * and what almost every app does
	 */
	public static final int SELECT = 0;

	/**
	 * Opens the clicked item, just as if it had been clicked outside of the action
	 * mode. This is what the Gmail app does
	 */
	public static final int OPEN = 1;

	private Set<Long> selection = new HashSet<Long>();

	private AdapterView<? super MultiChoiceBaseAdapter> adapterView;

	private BaseAdapter owner;

	private OnItemClickListener itemClickListener;

	private Drawable selectedItemBackground;

	private Drawable unselectedItemBackground;

	private Boolean itemIncludesCheckBox;

	/*
	 * Defines what happens when an item is clicked and the action mode was already active
	 */
	private int itemClickInActionModePolicy;

	MultiChoiceAdapterHelper(BaseAdapter owner) {
		this.owner = owner;
	}

	void restoreSelectionFromSavedInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		long[] array = savedInstanceState.getLongArray(BUNDLE_KEY);
		selection.clear();
		for (long id : array) {
			selection.add(id);
		}
	}

	void setAdapterView(AdapterView<? super BaseAdapter> adapterView) {
		this.adapterView = adapterView;
		checkActivity();
		adapterView.setOnItemLongClickListener(this);
		adapterView.setOnItemClickListener(this);
		adapterView.setAdapter(owner);
		extractBackgroundColor();
	}

	void checkActivity() {
		Context context = adapterView.getContext();
		if (context instanceof ListActivity) {
			throw new RuntimeException("ListView cannot belong to an activity which subclasses ListActivity");
		}
		if (context instanceof Activity || context instanceof ListActivity) {
			return;
		}
		throw new RuntimeException("ListView must belong to an activity which subclasses SherlockActivity");
	}

	void setOnItemClickListener(OnItemClickListener listener) {
		this.itemClickListener = listener;
	}

	void save(Bundle outState) {
		long[] array = new long[selection.size()];
		int i = 0;
		for (Long id : selection) {
			array[i++] = id;
		}
		outState.putLongArray(BUNDLE_KEY, array);
	}

	void select(long handle, boolean selected) {
		if (selected) {
			select(handle);
		} else {
			unselect(handle);
		}
	}

	void select(long handle) {
		boolean wasSelected = isSelected(handle);
		if (wasSelected) {
			return;
		}
		selection.add((long) handle);
		owner.notifyDataSetChanged();
		onItemSelectedStateChanged();
	}

	void unselect(long handle) {
		boolean wasSelected = isSelected(handle);
		if (!wasSelected) {
			return;
		}
		selection.remove(handle);
		if (getSelectionCount() == 0) {
			finishActionMode();
			return;
		}
		owner.notifyDataSetChanged();
		onItemSelectedStateChanged();
	}

	Set<Long> getSelection() {
		// Return a copy to prevent concurrent modification problems
		return new HashSet<Long>(selection);
	}

	int getSelectionCount() {
		return selection.size();
	}

	boolean isSelected(long handle) {
		return selection.contains(handle);
	}

	void finishActionMode() {
	}

	Context getContext() {
		return adapterView.getContext();
	}

	private void onItemSelectedStateChanged() {
		int count = getSelectionCount();
		if (count == 0) {
			finishActionMode();
			return;
		}
	}

	private void extractBackgroundColor() {
		Context ctx = getContext();
		int styleAttr = R.attr.multiChoiceAdapterStyle;
		int defStyle = R.style.MultiChoiceAdapter_DefaultSelectedItemStyle;
		TypedArray ta = ctx.obtainStyledAttributes(null, R.styleable.MultiChoiceAdapter, styleAttr, defStyle);
		selectedItemBackground = ta.getDrawable(R.styleable.MultiChoiceAdapter_selectedItemBackground);
		itemClickInActionModePolicy = ta.getInt(R.styleable.MultiChoiceAdapter_itemClickInActionMode, SELECT);
		ta.recycle();
		Resources res = ctx.getResources();
		unselectedItemBackground = new ColorDrawable(res.getColor(android.R.color.transparent));
	}

	//
	// OnItemLongClickListener implementation
	//

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		long handle = positionToSelectionHandle(position);
		boolean wasChecked = isSelected(handle);
		select(handle, !wasChecked);
		return true;
	}

	protected long positionToSelectionHandle(int position) {
		return position;
	}

	//
	// ActionMode.Callback related methods
	//

	void onDestroyActionMode() {
		selection.clear();
		owner.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		switch (itemClickInActionModePolicy) {
		case SELECT:
			onItemLongClick(adapterView, view, position, id);
			return;
		case OPEN:
			finishActionMode();
			break;
		default:
			throw new RuntimeException("Invalid \"itemClickInActionMode\" value: " + itemClickInActionModePolicy);
		}
		if (itemClickListener != null) {
			itemClickListener.onItemClick(adapterView, view, position, id);
		}
	}

	View getView(int position, View viewWithoutSelection) {
		if (itemIncludesCheckBox(viewWithoutSelection)) {
			initItemCheckbox(position, (ViewGroup) viewWithoutSelection);
		}
		updateItemBackground(position, viewWithoutSelection);
		return viewWithoutSelection;
	}

	private boolean itemIncludesCheckBox(View v) {
		if (itemIncludesCheckBox == null) {
			if (!(v instanceof ViewGroup)) {
				itemIncludesCheckBox = false;
			} else {
				ViewGroup root = (ViewGroup) v;
				itemIncludesCheckBox = root.findViewById(android.R.id.checkbox) != null;
			}
		}
		return itemIncludesCheckBox;
	}

	private void initItemCheckbox(int position, ViewGroup view) {
		CheckBox checkBox = (CheckBox) view.findViewById(android.R.id.checkbox);
		boolean selected = isSelected(position);
		checkBox.setTag(position);
		checkBox.setChecked(selected);
		checkBox.setOnCheckedChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	private void updateItemBackground(int position, View v) {
		long handle = positionToSelectionHandle(position);
		boolean selected = isSelected(handle);
		Drawable bg = selected ? selectedItemBackground : unselectedItemBackground;
		v.setBackgroundDrawable(bg);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int position = (Integer) buttonView.getTag();
		select(position, isChecked);
	}
}
