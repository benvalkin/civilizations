package com.uncreated.civilized.ui.components;

public interface IViewBuilder<T> {
   T buildView(int x, int y, int width, int height);
}
