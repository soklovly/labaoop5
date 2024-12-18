import java.io.*; // Импорт классов для работы с вводом/выводом данных
import java.net.*; // Импорт классов для работы с сетью (сокеты)

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // Адрес сервера (можно заменить на IP-адрес)
    private static final int PORT = 8030; // Порт для подключения к серверу

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) { // Создаем сокет для подключения к серверу по указанному адресу и порту
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in)); // Поток для чтения ввода с консоли
            PrintStream serverOutput = new PrintStream(socket.getOutputStream()); // Поток для отправки сообщений серверу
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Поток для получения сообщений от сервера

            // Создаем новый поток для получения сообщений от других клиентов через сервер
            new Thread(new Runnable() { // Создание нового потока с использованием анонимного класса Runnable
                @Override
                public void run() { // Реализация метода run, который будет выполняться в новом потоке
                    try {
                        String messageFromServer;
                        while ((messageFromServer = serverInput.readLine()) != null) { // Чтение сообщений от сервера
                            System.out.println(messageFromServer); // Вывод полученного сообщения на экран
                        }
                    } catch (IOException e) { // Обработка ошибок при чтении сообщений от сервера
                        System.out.println("Ошибка чтения сообщений от сервера: " + e); // Логирование ошибки
                    }
                }
            }).start(); // Запуск потока

            // Основной цикл программы для чтения сообщений с консоли и отправки их на сервер
            String message;
            while ((message = consoleInput.readLine()) != null) { // Чтение сообщения с консоли
                serverOutput.println(message); // Отправка сообщения серверу
                serverOutput.flush(); // Принудительное отправление сообщения на сервер
            }

        } catch (IOException e) { // Обработка ошибок, которые могут возникнуть при создании сокета или передаче данных
            System.out.println("Ошибка клиента: " + e); // Логирование ошибки клиента
        }
    }
}
