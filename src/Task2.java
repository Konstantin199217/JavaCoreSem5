
import java.io.File;
import java.util.Arrays;

public class Task2 {

    public static void main(String[] args) {
        print(new File("."), "", true);
    }

    static void print(File file, String indent, boolean isLast) {
        // Печатаем визуальное оформление
        System.out.print(indent);
        if (isLast) {
            System.out.print("└─");
            indent += "  ";
        } else {
            System.out.print("├─");
            indent += "│ ";
        }
        System.out.println(file.getName());

        // Проверяем, что коллекция не пуста.
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }

        // Добавляем счетчик общего кол-ва файлов в коллекции
        int subFilesTotal = 0;
        // Добавляем счетчик общего кол-ва директорий в коллекции
        int subDirTotal = 0;
        // Проходим по коллекции и подсчитываем кол-во файлов и директорий
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                subDirTotal++;
            else subFilesTotal++;
        }
        // Счетчики обработанных файлов и директорий
        int subDirCounter = 0;
        int subFileCounter = 0;
        // Проходим по коллекции
        for (int i = 0; i < files.length; i++) {
            // Сортируем коллекцию при помощи собственного компаратора, в начале массива идут директории, в конце файлы
            Arrays.sort(files, new FileComparator());
            // Если в коллекции обнаружена директория
            if (files[i].isDirectory()) {
                subDirCounter++;
                /*  Рекурсивно вызываем print(), в условие конечного файла добавил проверку,
                    что достигнут конец массива, без этой проверки линия к файлам прерывалась на последней директории.
                 */
                print(files[i], indent, (subDirCounter == subDirTotal && i == files.length - 1));
                // Если обнаружен файл
            } else {
                // Печатаем файлы.
                subFileCounter++;
                print(files[i], indent, subFileCounter == subFilesTotal);
            }
        }

    }

}
