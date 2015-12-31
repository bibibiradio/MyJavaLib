package com.bibibiradio.chain;

import java.util.Iterator;

/**
 * 双向链表实现<br>
 * 注意，该实现非线程安全<p>
 * @author 肖磊
 * @version 1.4
 */
public class DoublyLinkedList {
    /**
     * 链表头
     */
    private DoublyLinkedProxyData head;
    /**
     * 链表尾
     */
    private DoublyLinkedProxyData tail;
    
    /**
     * 通过首个链表项初始化链表，一般不显式使用
     * @param first 第一个链表项
     */
    public void init(DoublyLinkedProxyData first){
        first.setNext(null);
        first.setPre(null);
        head = tail = first;
    }
    
    /**
     * 重置／清空链表
     */
    public void reset(){
        head = tail = null;
    }
    
    /**
     * 生成obj的链表项插入到ele链表项前面
     * @param obj 待插入的链表项内容
     * @param ele 将待插入链表项插入到该链表项前面
     * @return 返回通过obj生成的链表项
     */
    public DoublyLinkedProxyData inputPreOfEle(Object obj,DoublyLinkedProxyData ele){
        DoublyLinkedProxyData linkedData = new DoublyLinkedProxyData(obj);
        linkedData.setPre(ele.getPre());
        linkedData.setNext(ele);
        
        if(head == ele){
            ele.setPre(linkedData);
            head = linkedData;
        }else{
            ele.getPre().setNext(linkedData);
            ele.setPre(linkedData);
        }
        
        return linkedData;
    }
    
    /**
     * 生成obj的链表项插入到ele链表项后面
     * @param obj 待插入的链表项内容
     * @param ele 将待插入链表项插入到该链表项后面
     * @return 返回通过obj生成的链表项
     */
    public DoublyLinkedProxyData inputNextOfEle(Object obj,DoublyLinkedProxyData ele){
        DoublyLinkedProxyData linkedData = new DoublyLinkedProxyData(obj);
        linkedData.setNext(ele.getNext());
        linkedData.setPre(ele);
        
        if(ele == tail){
            ele.setNext(linkedData);
            tail = linkedData;
        }else{
            ele.getNext().setPre(linkedData);
            ele.setNext(linkedData);
        }
        
        return linkedData;
    }
    
    /**
     * 生成obj的链表项插入到链表头部
     * @param obj 待插入的链表项内容
     * @return 返回通过obj生成的链表项
     */
    public DoublyLinkedProxyData inputPreOfHead(Object obj){
        if(head == null){
            DoublyLinkedProxyData linkedData = new DoublyLinkedProxyData(obj);
            init(linkedData);
            return linkedData;
        }else{
            return inputPreOfEle(obj,head);
        }
    }
    
    /**
     * 生成obj的链表项插入到链表尾部
     * @param obj 待插入的链表项内容
     * @return 返回通过obj生成的链表项
     */
    public DoublyLinkedProxyData inputNextOfTail(Object obj){
        if(tail == null){
            DoublyLinkedProxyData linkedData = new DoublyLinkedProxyData(obj);
            init(linkedData);
            return linkedData;
        }else{
            return inputNextOfEle(obj,tail);
        }
    }
    
    /**
     * 将参数ele从双向链表删除
     * @param ele 待删除的链表项
     */
    public void removeEleFromChain(DoublyLinkedProxyData ele){
        if(head == ele && tail == ele){
            head = tail = null;
        }else if(head == ele){
            head = ele.getNext();
            ele.getNext().setPre(null);
        }else if(tail == ele){
            tail = ele.getPre();
            ele.getPre().setNext(null);
        }else{
            ele.getNext().setPre(ele.getPre());
            ele.getPre().setNext(ele.getNext());
        }
    }
    
    /**
     * 将参数ele以及它前面的所有链表项从链表删除
     * @param ele
     */
    public void removeElesFromEleAndThePres(DoublyLinkedProxyData ele){
        if(tail == ele){
            reset();
        }else{
            head = ele.getNext();
            head.setPre(null);
        }
    }
    
    /**
     * 将参数ele以及它后面所有链表项从链表删除
     * @param ele
     */
    public void removeElesFromEleAndTheNexts(DoublyLinkedProxyData ele){
        if(head == ele){
            reset();
        }else{
            tail = ele.getPre();
            tail.setNext(null);
        }
    }
    
    /**
     * 获取从头遍历到尾的迭代器
     * @return 返回迭代器实例
     */
    public Iterator<DoublyLinkedProxyData> getForwardIterator(){
        return new DoublyLinkedListForwardIterator(head);
    }
    
    /**
     * 获取从尾遍历到头的迭代器
     * @return 返回迭代器实例
     */
    public Iterator<DoublyLinkedProxyData> getBackIterator(){
        return new DoublyLinkedListBackIterator(tail);
    }

    public DoublyLinkedProxyData getHead() {
        return head;
    }

    public void setHead(DoublyLinkedProxyData head) {
        this.head = head;
    }

    public DoublyLinkedProxyData getTail() {
        return tail;
    }

    public void setTail(DoublyLinkedProxyData tail) {
        this.tail = tail;
    }
}
