package com.bibibiradio.chain;

import java.util.Iterator;

public class DoublyLinkedListBackIterator implements Iterator<DoublyLinkedProxyData> {
    private DoublyLinkedProxyData curr;
    
    public DoublyLinkedListBackIterator(DoublyLinkedProxyData tail){
        curr = tail;
    }
    
    @Override
    public boolean hasNext() {
        // TODO Auto-generated method stub
        if(curr != null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public DoublyLinkedProxyData next() {
        // TODO Auto-generated method stub
        DoublyLinkedProxyData ret = curr;
        curr = curr.getPre();
        return ret;
    }

}
