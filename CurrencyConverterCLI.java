import java.util.Scanner;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class CurrencyConverterCLI {
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/[MyAPIKYE]/latest/BRL";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao Conversor de Moedas!");
        System.out.println("Escolha uma das seguintes moedas para conversão:");
        System.out.println("1. Euro (EUR)");
        System.out.println("2. Libra Esterlina (GBP)");
        System.out.println("3. Iene Japonês (JPY)");
        System.out.println("4. Dólar Canadense (CAD)");
        System.out.println("5. Real Brasileiro (BRL)");

        int choice = scanner.nextInt();
        String currencyCode = getCurrencyCode(choice);

        if (currencyCode == null) {
            System.out.println("Escolha inválida. Encerrando o programa.");
            return;
        }

        System.out.println("Digite o valor em dólares (USD) que deseja converter:");
        double amount = scanner.nextDouble();

        try {
            double rate = getExchangeRate(currencyCode);
            double convertedAmount = amount * rate;
            System.out.printf("Cotação atual do Real para %s: %.4f\n", currencyCode, rate);
            System.out.printf("Valor convertido: %.2f %s\n", convertedAmount, currencyCode);
        } catch (IOException e) {
            System.out.println("Erro ao obter a taxa de câmbio. Por favor, tente novamente mais tarde.");
        }
    }

    private static String getCurrencyCode(int choice) {
        return switch (choice) {
            case 1 -> "EUR";
            case 2 -> "GBP";
            case 3 -> "JPY";
            case 4 -> "CAD";
            case 5 -> "USD";
            default -> null;
        };
    }

    private static double getExchangeRate(String currencyCode) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Erro na conexão com a API");
        }

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder inline = new StringBuilder();
        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }
        scanner.close();

        JSONObject jsonObject = new JSONObject(inline.toString());
        JSONObject conversionRates = jsonObject.getJSONObject("conversion_rates");
        return conversionRates.getDouble(currencyCode);
    }
}
