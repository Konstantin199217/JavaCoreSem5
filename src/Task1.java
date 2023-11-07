import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * 1. Создать 2 текстовых файла, примерно по 50-100 символов в каждом.
 * 2. Написать метод, "склеивающий" эти файлы, т.е. вначале идет текст из первого файла, потом из второго.
 * 3. Написать метод, который проверяет, присутствует ли указанное пользователем солово в файле.
 * 4. Написать метод, проверяющий, есть ли указанное слово в папке.
 * ДЗ. Написать функцию, создающую резервную копию всех файлов в директории во вновь созданную папку ./backup
 */

public class Task1 {

    private static final int CHAN_MIN = 65;
    private static final int CHAN_MAX = 90;
    private static final String TO_SEARCH = "GeekBrains";
    private static final Random random = new Random();
    private static final String DIR_BACKUP = "./backup";

    public static void main(String[] args) {
        // Копирование отдельного файла
//        backup("./src/main/java/org/example/Task1.java", DIR_BACKUP);
        // Копирование директории
        backup("./src", DIR_BACKUP);

    }

    /**
     * Создание резервной копии файла или директории.
     *
     * @param dirSource файл или директория для копирования.
     * @param dirTarget директория для сохранения копии.
     */
    public static void backup(String dirSource, String dirTarget) {
        // Сохраняем пути к файлам в переменные
        Path pathSource = Paths.get(dirSource);
        Path pathTarget = Paths.get(dirTarget);
        // Проверяем, если директория в которую будет производиться копирование существует, удаляем ее.
        if (Files.exists(pathTarget)) {
            File file = new File(dirTarget);
            delete(file);
        }

        // Проверяем передан ли для копирования файл
        if (Files.isRegularFile(pathSource)) {
            try {
                // Создаем директорию
                Files.createDirectory(pathTarget);
                // Копируем файл в созданную директорию
                Files.copy(pathSource, pathTarget.resolve(pathSource.getFileName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Если получен не файл, значит получена директория
        else {
            // Производим копирование файлов. Получаем стрим со вложенными директориями и файлами.
            try (Stream<Path> pathStream = Files.walk(pathSource)) {
                // Перебираем полученный стрим.
                pathStream.forEach(el -> {
                    try {
                    /*  Производим копирование эл-тов стрима в папку backup.
                        Строим путь используя метод resolve():
                        1. К файлу подлежащему копированию добавляем путь до исходной папки копирования
                            (пример src\main\java\org\example + Main.java)
                        2. Добавляем к полученному пути, путь до директории в которую копируем
                            (пример .\backup + src\main\java\org\example\Main.java)
                     */
                        Files.copy(el, pathTarget.resolve(pathSource.relativize(el)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Создание резервной копии завершено.");
    }

    /**
     * Служебный метод рекурсивно удаляющий файл или директорию.
     *
     * @param file файл или директория.
     */
    private static void delete(File file) {
        // Получаем вложенные директории для переданного файла
        File[] files = file.listFiles();
        // Проверяем есть ли вложенные директории
        if (files != null) {
            // Вложенные директории перебираем
            for (File el : files) {
                // Для каждой вложенной директории рекурсивно вызываем метод удаления
                delete(el);
            }
        }
        // Если вложенных директорий нет, значит работаем с файлом. Удаляем его.
        file.delete();
    }

    /**
     * Генерация набора символов.
     *
     * @param amount длина строки.
     * @return строка случайных символов.
     */
    public static String generatedText(int amount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            sb.append((char) random.nextInt(CHAN_MIN, CHAN_MAX + 1));
        }
        return sb.toString();
    }

    /**
     * Запись данных в файл.
     *
     * @param nameFile имя файла.
     * @param length   длина данных.
     */
    public static void writeFileContents(String nameFile, int length) {
        try (FileOutputStream outputStream = new FileOutputStream(nameFile)) {
            outputStream.write(generatedText(length).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Запись данных в файл.
     *
     * @param nameFile имя файла.
     * @param length   длина данных в файле.
     * @param words    количество определенных слов.
     */
    public static void writeFileContents(String nameFile, int length, int words) {
        try (FileOutputStream outputStream = new FileOutputStream(nameFile)) {
            for (int i = 0; i < words; i++) {
                if (random.nextInt(2) == 0) {
                    outputStream.write(TO_SEARCH.getBytes());
                } else {
                    outputStream.write(generatedText(length).getBytes());
                }
            }
            outputStream.write(generatedText(length).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Склейка двух файлов
     *
     * @param file1   файл 1
     * @param file2   файл 2
     * @param fileOut результирующий файл.
     */
    public static void concatenate(String file1, String file2, String fileOut) {
        try (FileOutputStream outputStream = new FileOutputStream(fileOut)) {
            int c;
            // Запись первого файла
            try (FileInputStream inputStream = new FileInputStream(file1)) {
                while ((c = inputStream.read()) != -1) {
                    outputStream.write(c);
                }
            }
            // Запись второго файла.
            try (FileInputStream inputStream = new FileInputStream(file2)) {
                while ((c = inputStream.read()) != -1) {
                    outputStream.write(c);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Поиск слова в файле.
     *
     * @param fileName файл для поиска.
     * @param word     солово для поиска.
     * @return true - если слово найдено, иначе false.
     */
    public static boolean findInFile(String fileName, String word) {
        try (FileInputStream inputStream = new FileInputStream(fileName)) {
            byte[] wordSearch = word.getBytes();
            int count = 0;
            int c;
            while ((c = inputStream.read()) != -1) {
                if (c == wordSearch[count]) {
                    count++;
                } else {
                    count = 0;
                    if (c == wordSearch[count]) count++;
                }
                if (count == word.length()) return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Получение списка файлов в директории, в которых встречается искомое слово.
     *
     * @param dir    директория для поиска.
     * @param search слово для поиска.
     * @return список файлов.
     * @throws IOException при ошибке работы с файлом.
     */
    static List<String> searchMatch(File dir, String search) throws IOException {
        List<String> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null)
            return list;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                continue;
            if (findInFile(files[i].getCanonicalPath(), search)) {
                list.add(files[i].getName());
            }
        }
        return list;
    }
}
