package com.bibibiradio.chain;

import java.util.Iterator;

public class DoublyLinkedListForwardIterator implements Iterator<DoublyLinkedProxyData> {
    private DoublyLinkedProxyData curr;
    
    public DoublyLinkedListForwardIterator(DoublyLinkedProxyData head){
        curr = head;
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
        curr = curr.getNext();
        return ret;
    }

}
