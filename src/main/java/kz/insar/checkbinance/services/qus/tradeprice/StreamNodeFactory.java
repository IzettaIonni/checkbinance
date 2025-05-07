package kz.insar.checkbinance.services.qus.tradeprice;

import java.util.List;

public interface StreamNodeFactory {

    StreamNode createStreamNode();
    default StreamNode createStreamNodeAndAddItem(String item) {
        var streamNode = createStreamNode();
        streamNode.addItem(item);
        return streamNode;
    }

    default StreamNode createStreamNodeAndAddItems(List<String> items) {
        var streamNode = createStreamNode();
        streamNode.addItems(items);
        return streamNode;
    }

}
