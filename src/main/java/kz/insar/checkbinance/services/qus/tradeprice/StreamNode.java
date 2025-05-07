package kz.insar.checkbinance.services.qus.tradeprice;

import java.util.List;

public interface StreamNode {

    void addItem(String item);
    void removeItem(String item);
    void close();
    List<String> getItems();
    default int getItemsCount() {
        return getItems().size();
    }

    default void addItems(List<String> items) {
        for (var item : items) addItem(item);
    }
    default boolean isContainsItem(String item) {
        return getItems().contains(item);
    }

}
