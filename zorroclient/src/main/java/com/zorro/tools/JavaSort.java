package com.zorro.tools;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */

import java.util.LinkedList;

public class JavaSort {


    /**
     * 插入排序
     * @param numbers
     */
    private void insertSort(LinkedList<Integer> numbers) {
        int size = numbers.size();
        Integer temp = null;
        int j = 0;

        for (int i = 0; i < size; i++) {
            temp = numbers.get(i);
            //假如temp比前面的值小，则将前面的值后移
            for (j = i; j > 0 && temp< numbers.get(j - 1); j--) {
                numbers.add(j - 1,temp);
                numbers.remove(j + 1);
            }
        }
    }
}
