import java.io.*; // Импорт классов для работы с вводом/выводом данных
import java.net.*; // Импорт классов для работы с сетью (сокеты)
import java.util.*; // Импорт утилит для работы с коллекциями

public class Server {
    private static Map<Integer, PrintStream> clients = new HashMap<>(); // Карта для хранения клиентов (id -> поток)
    private static final int PORT = 8030; // Порт, на котором сервер слушает подключения

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Создаём серверный сокет
            System.out.println("Сервер запущен, ожидаю подключений..."); // Выводим сообщение о запуске сервера

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Ожидаем подключения клиента
                new ClientHandler(clientSocket).start(); // Создаём и запускаем поток для обработки клиента
            }
        } catch (IOException e) {
            System.out.println("Ошибка сервера: " + e); // Обрабатываем ошибки сервера
        }
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket; // Сокет для связи с клиентом
        private BufferedReader in; // Поток для чтения данных от клиента
        private PrintStream out; // Поток для отправки данных клиенту
        private int clientId; // Уникальный идентификатор клиента

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket; // Инициализируем сокет клиента
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Создаём поток для чтения данных
                out = new PrintStream(clientSocket.getOutputStream()); // Создаём поток для отправки данных

                clientId = clientSocket.getPort(); // Устанавливаем уникальный идентификатор клиента
                synchronized (clients) {
                    clients.put(clientId, out); // Добавляем клиента в карту
                }

                System.out.println("Клиент " + clientId + " подключен."); // Логируем подключение клиента

                String message;
                while ((message = in.readLine()) != null) { // Читаем сообщения от клиента
                    System.out.println("Получено от клиента " + clientId + ": " + message); // Логируем сообщение
                    sendMessageToOtherClients(clientId, message); // Отправляем сообщение другим клиентам
                }
            } catch (IOException e) {
                System.out.println("Ошибка обработки клиента " + clientId + ": " + e); // Логируем ошибку клиента
            } finally {
                try {
                    synchronized (clients) {
                        clients.remove(clientId); // Удаляем клиента из карты
                    }
                    clientSocket.close(); // Закрываем сокет клиента
                    System.out.println("Клиент " + clientId + " отключен."); // Логируем отключение клиента
                } catch (IOException e) {
                    System.out.println("Ошибка при закрытии сокета клиента " + clientId + ": " + e); // Логируем ошибку закрытия
                }
            }
        }

        private void sendMessageToOtherClients(int senderId, String message) {
            synchronized (clients) { // Синхронизация доступа к карте клиентов
                for (Map.Entry<Integer, PrintStream> entry : clients.entrySet()) { // Перебираем всех клиентов
                    if (entry.getKey() != senderId) { // Исключаем отправителя
                        entry.getValue().println("От клиента " + senderId + ": " + message); // Отправляем сообщение
                        entry.getValue().flush(); // Принудительно отправляем данные
                    }
                }
            }
        }
    }
}
