package yahoofinance.quotes.query1v7;

import com.fasterxml.jackson.databind.JsonNode;
import yahoofinance.Stock;
import yahoofinance.Utils;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

import java.math.BigDecimal;
import java.util.TimeZone;

/**
 *
 * @author Stijn Strickx
 */
public class StockQuotesQuery1V7Request extends QuotesRequest<Stock> {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    
    public StockQuotesQuery1V7Request(String symbols) {
        super(symbols);
    }

    @Override
    protected Stock parseJson(JsonNode node) {
        String symbol = node.get("symbol").asText();
        Stock stock = new Stock(symbol);

        stock.setName(node.get("longName").asText());
        stock.setCurrency(node.get("currency").asText());
        stock.setStockExchange(node.get("fullExchangeName").asText());

        stock.setQuote(this.getQuote(node));
        stock.setStats(this.getStats(node));
        stock.setDividend(this.getDividend(node));

        return stock;
    }

    private String getStringValue(JsonNode node, String field) {
        if(node.has(field)) {
            return node.get(field).asText();
        }
        return null;
    }

    private StockQuote getQuote(JsonNode node) {
        String symbol = node.get("symbol").asText();
        StockQuote quote = new StockQuote(symbol);

        quote.setPrice(Utils.getBigDecimal(getStringValue(node,"regularMarketPrice")));
        // quote.setLastTradeSize(null);
        quote.setAsk(Utils.getBigDecimal(getStringValue(node,"ask")));
        quote.setAskSize(Utils.getLong(getStringValue(node,"askSize")));
        quote.setBid(Utils.getBigDecimal(getStringValue(node,"bid")));
        quote.setBidSize(Utils.getLong(getStringValue(node,"bidSize")));
        quote.setOpen(Utils.getBigDecimal(getStringValue(node,"regularMarketOpen")));
        quote.setPreviousClose(Utils.getBigDecimal(getStringValue(node,"regularMarketPreviousClose")));
        quote.setDayHigh(Utils.getBigDecimal(getStringValue(node,"regularMarketDayHigh")));
        quote.setDayLow(Utils.getBigDecimal(getStringValue(node,"regularMarketDayLow")));

        quote.setTimeZone(TimeZone.getTimeZone(getStringValue(node,"exchangeTimezoneName")));
        if(node.has("regularMarketTime")) {
            quote.setLastTradeTime(Utils.unixToCalendar(node.get("regularMarketTime").asLong()));
        }

        quote.setYearHigh(Utils.getBigDecimal(getStringValue(node,"fiftyTwoWeekHigh")));
        quote.setYearLow(Utils.getBigDecimal(getStringValue(node,"fiftyTwoWeekLow")));
        quote.setPriceAvg50(Utils.getBigDecimal(getStringValue(node,"fiftyDayAverage")));
        quote.setPriceAvg200(Utils.getBigDecimal(getStringValue(node,"twoHundredDayAverage")));

        quote.setVolume(Utils.getLong(getStringValue(node,"regularMarketVolume")));
        quote.setAvgVolume(Utils.getLong(getStringValue(node,"averageDailyVolume3Month")));

        return quote;
    }

    private StockStats getStats(JsonNode node) {
        String symbol = getStringValue(node,"symbol");
        StockStats stats = new StockStats(symbol);

        stats.setMarketCap(Utils.getBigDecimal(getStringValue(node,"marketCap")));
        // stats.setSharesFloat(Utils.getLong(getStringValue(node,"sharesOutstanding")));
        stats.setSharesOutstanding(Utils.getLong(getStringValue(node,"sharesOutstanding")));
        // stats.setSharesOwned(Utils.getLong(getStringValue(node,"symbol")));

        stats.setEps(Utils.getBigDecimal(getStringValue(node,"epsTrailingTwelveMonths")));
        stats.setPe(Utils.getBigDecimal(getStringValue(node,"trailingPE")));
        // stats.setPeg(Utils.getBigDecimal(getStringValue(node,"symbol")));

        stats.setEpsEstimateCurrentYear(Utils.getBigDecimal(getStringValue(node,"epsForward")));
        // stats.setEpsEstimateNextQuarter(Utils.getBigDecimal(getStringValue(node,"symbol")));
        // stats.setEpsEstimateNextYear(Utils.getBigDecimal(getStringValue(node,"symbol")));

        stats.setPriceBook(Utils.getBigDecimal(getStringValue(node,"priceToBook")));
        // stats.setPriceSales(Utils.getBigDecimal(getStringValue(node,"symbol")));
        stats.setBookValuePerShare(Utils.getBigDecimal(getStringValue(node,"bookValue")));

        // stats.setOneYearTargetPrice(Utils.getBigDecimal(getStringValue(node,"symbol")));
        // stats.setEBITDA(Utils.getBigDecimal(getStringValue(node,"symbol")));
        // stats.setRevenue(Utils.getBigDecimal(getStringValue(node,"symbol")));

        // stats.setShortRatio(Utils.getBigDecimal(getStringValue(node,"symbol")));

        return stats;
    }

    private StockDividend getDividend(JsonNode node) {
        String symbol = getStringValue(node,"symbol");
        StockDividend dividend = new StockDividend(symbol);

        if(!node.has("dividendDate")) {
            return dividend;
        }

        long dividendTimestamp = node.get("dividendDate").asLong();
        dividend.setPayDate(Utils.unixToCalendar(dividendTimestamp));
        // dividend.setExDate(Utils.unixToCalendar(node.get("dividendDate").asLong()));
        dividend.setAnnualYield(Utils.getBigDecimal(getStringValue(node,"trailingAnnualDividendRate")));
        BigDecimal yield = Utils.getBigDecimal(getStringValue(node,"trailingAnnualDividendYield"));
        if(yield != null) {
            dividend.setAnnualYieldPercent(yield.multiply(ONE_HUNDRED));
        }

        return dividend;
    }

}
