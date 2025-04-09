import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.Scanner;

public class ConversorDeMoeda {

    // Substitua com sua chave da API
    private static final String API_KEY = "SUA_CHAVE_AQUI";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Moeda base (ex: USD): ");
        String base = scanner.nextLine().toUpperCase();

        System.out.print("Moeda destino (ex: BRL): ");
        String destino = scanner.nextLine().toUpperCase();

        System.out.print("Valor a converter: ");
        double valor = scanner.nextDouble();

        try {
            double taxa = obterTaxaDeCambio(base, destino);
            double convertido = valor * taxa;
            System.out.printf("Valor convertido: %.2f %s%n", convertido, destino);
        } catch (Exception e) {
            System.out.println("Erro ao converter: " + e.getMessage());
        }

        scanner.close();
    }

    public static double obterTaxaDeCambio(String base, String destino) throws Exception {
        String urlStr = API_URL + base;

        URL url = new URL(urlStr);
        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
        conexao.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        String inputLine;
        StringBuilder resposta = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            resposta.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(resposta.toString());

        if (!json.getString("result").equals("success")) {
            throw new Exception("Falha ao obter taxas de câmbio");
        }

        JSONObject rates = json.getJSONObject("conversion_rates");

        if (!rates.has(destino)) {
            throw new Exception("Moeda de destino não encontrada");
        }

        return rates.getDouble(destino);
    }
}
