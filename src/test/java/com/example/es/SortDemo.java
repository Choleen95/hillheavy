package com.example.es;

import org.junit.Test;

/**
 * 功能描述
 *
 * @author [山沉]
 * @PersonalBlog [https://choleen95.github.io/]
 * @博客 [https://www.cnblogs.com/Choleen/]
 * @since [2023/3/19 10:25]
 */
public class SortDemo {

    private static ThreadLocal<String> local = new ThreadLocal<>();

    @Test
    public void bubbleSort() {
        int[] array = {3,2,5,1,6,4};
        for (int i = 0;i < array.length;i++) {
            for(int j = 0;j < array.length - 1 - i;j++) {
                if (array[j + 1] < array[j]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
            System.out.println("第"+i+"步排序的结果：");
            for (int k = 0;k < array.length;k++) {
                System.out.print(array[k] + " ");
            }
            System.out.print("\n");
        }
        for (int i = 0;i < array.length;i++) {
            System.out.print(array[i] + " ");
        }
    }

    @Test
    public void testThread() {
        local.set("mainThread");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程："+local.get());
            }
        });
        thread.start();
        System.out.println("主线程："+local.get());
    }
}
