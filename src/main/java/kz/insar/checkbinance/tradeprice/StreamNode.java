package kz.insar.checkbinance.tradeprice;

import java.util.List;

public interface StreamNode {

    void addItem(String item);
    void removeItem(String item);
    List<String> getItems();
    default int getItemsCount() {
        return getItems().size();
    }

    default void addItems(List<String> items) {
        for (var item : items) addItem(item);
    }

}
