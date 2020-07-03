package com.company;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.ToIntFunction;

public class RadixSort {

    /**
     * Находит максимальное значение объекта в массиве.
     * @param data Входой массив типа Т
     * @param toIntConverter Конвертер объекта в целое число
     * @param <T> Тип объекта
     * @return Максимальное значение объекта
     */
    private static <T> int getMax(T[] data, ToIntFunction<T> toIntConverter) {
        int max = toIntConverter.applyAsInt(data[0]);
        for (T value : data) {
            int intValue = toIntConverter.applyAsInt(value);
            if (intValue > max) {
                max = intValue;
            }
        }
        return max;
    }

    /**
     * Находит минимальное значение объекта в массиве.
     * @param data Входой массив типа Т
     * @param toIntConverter Конвертер объекта в целое число
     * @param <T> Тип объекта
     * @return Минимальное значение объекта
     */
    private static <T> int getMin(T[] data, ToIntFunction<T> toIntConverter) {
        int min = toIntConverter.applyAsInt(data[0]);
        for (T value : data) {
            int intValue = toIntConverter.applyAsInt(value);
            if (intValue < min) {
                min = intValue;
            }
        }
        return min;
    }

    /**
     * Сортировка по цифре
     * @param data Входной массив
     * @param exp exp = digitBase^i
     * @param digitBase Основание системы счисления
     * @param toIntConverter Конвертер объекта в целое число
     * @param <T> Тип объекта
     */
    private static <T> void countingSort(T[] data, int exp, int digitBase, ToIntFunction<T> toIntConverter)
    {
        int size = data.length;

        T[] output =  (T[]) Array.newInstance(data.getClass().getComponentType(), size);
        int[] counts = new int[digitBase];

        for (T value : data) {                                      // Подсчет количества различных разрядов для exp в count[]
            int intValue = toIntConverter.applyAsInt(value);
            int index = (intValue / exp) % digitBase;
            counts[index]++;
        }

        for (int i = 1; i < digitBase; i++) {      // модификация count таким образом, чтобы count[i] указывал на первую позицию в output чисел с цифрой i в обрабатываемом разряде
            counts[i] += counts[i - 1];
        }

        for (int i = size - 1; i >= 0; i--) {      // Формирование результирующего массива
            int intValue = toIntConverter.applyAsInt(data[i]);
            int index = (intValue / exp) % digitBase;
            output[counts[index] - 1] = data[i];
            counts[index]--;
        }

        System.arraycopy(output, 0, data, 0, size);  // Копирование отсортированных по соответствующему разряду данных в исходный массив
    }

    /**
     * RadixSort массива элементов, которые могут быть представлены в виде целых чисел.
     * @param data Входной массив типа Т
     * @param digitBase Основание системы счисления
     * @param toIntConverter Конвертер объекта в целое число
     * @param <T> Тип элемента массива
     */
    public static <T> void sort(T[] data, int digitBase, ToIntFunction<T> toIntConverter)
    {
        int max = getMax(data, toIntConverter);
        int min = getMin(data, toIntConverter);

        for (int exp = 1; max / exp > 0; exp *= digitBase) {           // поразрядная сортировка для каждого разрада по основанию
            countingSort(data, exp, digitBase, toIntConverter);   // digitBase вместо номера разряда используется exp = digitBase ^ (номер разряда)
        }
    }

    /**
     * RadixSort массива Integer
     * @param data Входной массив типа Т
     * @param digitBase Основание системы счисления
     */
    public static void sort(Integer[] data, int digitBase) {
        sort(data, digitBase, x -> x);
    }
}
