package com.example.a835127729qqcom.photodealdemo;

import java.util.LinkedList;

/**
 * Created by 835127729qq.com on 16/11/8.
 */
public class NotifyLinkedList<E> extends LinkedList<E>{
    private LinkedListOperateListner mLinkedListOperateListner;

    public interface LinkedListOperateListner{
        void notifyAdd();
        void notifyremove();
    }

    @Override
    public boolean add(E object) {
        boolean res = super.add(object);
        if(mLinkedListOperateListner!=null){
            mLinkedListOperateListner.notifyAdd();
        }
        return res;
    }

    @Override
    public E removeLast() {
        E res = super.removeLast();
        if(mLinkedListOperateListner!=null){
            mLinkedListOperateListner.notifyremove();
        }
        return res;
    }

    @Override
    public boolean remove(Object object) {
        boolean res = super.remove(object);
        if(mLinkedListOperateListner!=null){
            mLinkedListOperateListner.notifyremove();
        }
        return res;
    }

    public void setmLinkedListOperateListner(LinkedListOperateListner mLinkedListOperateListner) {
        this.mLinkedListOperateListner = mLinkedListOperateListner;
    }
}