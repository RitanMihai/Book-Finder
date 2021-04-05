package services.util;

import com.google.protobuf.Descriptors;
import proto.BookOuterClass;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class BookUtil {
    public static List<BookOuterClass.Book> filterList(List<BookOuterClass.Book> books, Map<Descriptors.FieldDescriptor, Object> filters) {
        /* *
         * As you can see we got the field names, we know that for each of this field gRPC framework generate
         * getters and setters following this pattern: getFieldName(), setFieldName().
         * Ex. field title: getTitle(), setTitle.
         * That being said we will use java Reflection to dynamically filter our list
         * */

        System.out.println("Requested filters are : ");
        Map<String, Object> methods = new HashMap<>();
        filters.forEach((fieldDescriptor, o) ->
                System.out.println(" ( " + o.getClass() + " ) " + fieldDescriptor.getName() + " : " + o));

        System.out.println("Create a Map of Methods and their values ... ");
        filters.forEach(((fieldDescriptor, o) -> {
            /* Construct methods name */
            final String getter = "get";
            String fieldName = fieldDescriptor.getName();
            String methodName = getter + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

            methods.put(methodName, o);
        }));

        System.out.println(methods);

        books = books.stream().filter(book -> {
            boolean isValid = true;

            for (Map.Entry<String, Object> entry : methods.entrySet()) {
                String s = entry.getKey();
                Object o = entry.getValue();

                try {
                    isValid = book.getClass().getMethod(s, null).invoke(book).equals(o);
                    if (!isValid)
                        break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            return isValid;
        }).collect(Collectors.toList());

        return books;
    }

    /***
     * If both parameters are null the entire library will be returned.
     * If firstBook is null and lastBook not, then list from 0 to lastBook will be returned.
     * If lastBook is null and firstBook not, then list from lastBook to the list size (@param books) will be returned.
     * If both have values then a slice between them will be returned.
     * @param books
     * @param firstBook start slicing the list from this index
     * @param lastBook end slicing the list at this index
     * @return sliced list
     *
     */
    public static List<BookOuterClass.Book> pagination(List<BookOuterClass.Book> books, Object firstBook, Object lastBook) {
        boolean isPaginationRequested = !(firstBook == null && lastBook == null);

        if (!isPaginationRequested)
            return books;

        int firstBookIndex = firstBook == null || (int) firstBook < 0 || (int) firstBook > books.size() ? 0 : (int) firstBook;
        int lastBookIndex = lastBook == null || (int) lastBook < 0 ? 0 : (int) lastBook;

        if (firstBookIndex == 0 && lastBookIndex == 0)
            return new ArrayList<>();

        if (lastBookIndex > books.size() && firstBookIndex > books.size())
            return new ArrayList<>();

        if (lastBookIndex > books.size())
            lastBookIndex = books.size();

        /* Return reverse list */
        if (firstBookIndex > lastBookIndex) {
            List<BookOuterClass.Book> reversedList = books.subList(lastBookIndex, firstBookIndex);
            Collections.reverse(reversedList);
            return reversedList;
        }

        return books.subList(firstBookIndex, lastBookIndex);
    }
}