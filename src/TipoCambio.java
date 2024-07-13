import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

public class TipoCambio {
    static final String key = "9e4afc66dc7312f029761bf3";
    static final String URL = "https://v6.exchangerate-api.com/v6/" + key;
    static java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder().
            setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    static ListaCambio codes;
    static HttpRequest currencyDatarequest = HttpRequest.newBuilder().uri(URI.create(URL + "/codes")).build();

    String lastExchangeRate1 = null;
    String lastExchangeRate2 = null;

    // currency data -  https://v6.exchangerate-api.com/v6/YOUR-API-KEY/codes
    // pairing       - https://v6.exchangerate-api.com/v6/YOUR-API-KEY/pair/EUR/GBP";
    // pairing numb  - https://v6.exchangerate-api.com/v6/YOUR-API-KEY/pair/EUR/GBP/AMOUNT

    static public void showSupportedCurrencies() {
        int annex = 4;
        for (int i = 0; i < codes.supportedCodes.length; i++) {
            String s = codes.supportedCodes[i].get(0) + " (" + codes.supportedCodes[i].get(1) + ")";
            System.out.printf("%-40s", s);
            if (i % annex == 0)
                System.out.println();
        }
        System.out.println("\n^Monedas soportadas^");
    }

    static public void requestCurrencyData() {
        try {
            HttpResponse<String> response = client.send(currencyDatarequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            System.out.println(response.body());
            codes = gson.fromJson(response.body(), ListaCambio.class);
            System.out.println(Arrays.toString(codes.supportedCodes));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean requestExchange(double amount, String from, String to) {
        HttpRequest request;
        if (amount < 1) amount = 1;

        try {
            if (from != null && to != null) {
                request = HttpRequest.newBuilder().uri(URI.create(URL + "/pair/" + from + "/" + to + "/" + amount)).build();
            }
            else {
                request = HttpRequest.newBuilder().uri(URI.create(URL + "/pair/" + lastExchangeRate1 + "/" + lastExchangeRate2 + "/" + amount)).build();
            }
            if (request == null)
                return false;

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            InterCambio exchangePair = gson.fromJson(response.body(), InterCambio.class);
            if (Objects.equals(exchangePair.result, "success")) {
                lastExchangeRate1 = from;
                lastExchangeRate2 = to;
                System.out.printf("%.4f%s = %.4f%s (x%.4f) - %s %n",
                        amount, exchangePair.baseCode, exchangePair.conversionResult, exchangePair.targetCode, exchangePair.conversionRate, LocalTime.now());
                return true;
            }
            else
                return false;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
