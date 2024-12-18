import java.io.*; // Импорт классов для работы с вводом/выводом данных
import java.net.*; // Импорт классов для работы с сетью (сокеты)

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // Адрес сервера (можно заменить на IP-адрес)
    private static final int PORT = 8030; // Порт для подключения к серверу

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT)) { // Создаём сокет для подключения к серверу
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in)); // Поток для чтения ввода с консоли
            PrintStream serverOutput = new PrintStream(socket.getOutputStream()); // Поток для отправки сообщений серверу
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Поток для получения сообщений от сервера

            // Создаём поток для получения сообщений от других клиентов через сервер
            new Thread(() -> {
                try {
                    String messageFromServer;
                    while ((messageFromServer = serverInput.readLine()) != null) { // Читаем сообщения от сервера
                        System.out.println(messageFromServer); // Выводим сообщение на экран
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка чтения сообщений от сервера: " + e); // Логируем ошибки чтения
                }
            }).start();

            // Основной цикл: читаем сообщения с консоли и отправляем их на сервер
            String message;
            while ((message = consoleInput.readLine()) != null) { // Читаем сообщение с консоли
                serverOutput.println(message); // Отправляем сообщение серверу
                serverOutput.flush(); // Принудительно отправляем данные
            }

        } catch (IOException e) {
            System.out.println("Ошибка клиента: " + e); // Логируем ошибки клиента
        }
    }
}
