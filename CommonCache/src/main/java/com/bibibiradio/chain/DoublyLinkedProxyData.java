package com.bibibiradio.chain;

public class DoublyLinkedProxyData {
    private DoublyLinkedProxyData pre;
    private DoublyLinkedProxyData next;
    protected Object realData;
    public DoublyLinkedProxyData(Object realData){
        this.realData = realData;
    }
    public DoublyLinkedProxyData(){
     
    }
    public DoublyLinkedProxyData getPre() {
        return pre;
    }
    public void setPre(DoublyLinkedProxyData pre) {
        this.pre = pre;
    }
    public DoublyLinkedProxyData getNext() {
        return next;
    }
    public void setNext(DoublyLinkedProxyData next) {
        this.next = next;
    }
    public Object getRealData() {
        return realData;
    }
    public void setRealData(Object realData) {
        this.realData = realData;
    }
    
    
}
