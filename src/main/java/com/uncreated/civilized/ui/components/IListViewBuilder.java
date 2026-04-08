package com.uncreated.civilized.ui.components;

import java.util.List;

public interface IListViewBuilder<Model, ElementWidget> {
   List<Model> provideModelData();
   ElementWidget buildElementWidgetFromModel(int elementIndex, Model model, int elementX, int elementY, int elementWidth, int elementHeight, int elementSpacing);
}
