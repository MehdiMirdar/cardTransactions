package ir.dotin.card_transactions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CardTransactionControllerIT {
    // these fields must check and update before every test
    public Long trackingNumber = 1211111111114L;
    public String transactionDate = "2020-11-03 14:59:28";
    public String startDate = "2020-11-01 10:15:28";
    public String endDate = "2020-11-02 14:59:28";
    ///////////////////////////////////////////////////////
    HttpHeaders headers;
    JSONObject obj;
    @BeforeEach
    public void before() {
        headers = new HttpHeaders();
        obj = new JSONObject();
        obj.put("cardNumber", 1111111111111128L);
        obj.put("trackingNumber", trackingNumber);
        obj.put("password", "1128");
        obj.put("transactionDate", transactionDate);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void cardBalanceTest_isOK_whenResponseCodeIs77() throws Exception {
        obj.put("transactionDate", "2020-11-01 14:59:28");
        obj.put("terminalType", "02");
        String url = "/cardbalance";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"77\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardBalanceTest_isOK_whenResponseCodeIs00() throws Exception {
        obj.put("terminalType", "02");
        String url = "/cardbalance";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"00\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardBalanceTest_isOK_whenResponseCodeIs94() throws Exception {
        obj.put("terminalType", "02");
        String url = "/cardbalance";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"94\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardBalanceTest_isOK_whenResponseCodeIs57() throws Exception {
        obj.put("password", "1127");
        obj.put("terminalType", "02");
        String url = "/cardbalance";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"57\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardBalanceTest_isOK_whenResponseCodeIs15() throws Exception {
        obj.put("cardNumber", 1000111111111128L);
        obj.put("terminalType", "02");
        String url = "/cardbalance";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"15\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardBalanceTest_isOK_whenResponseCodeIs12() throws Exception {
        String url = "/cardbalance";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"12\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardBalanceTest_isOK_whenResponseCodeIs80() throws Exception {
        obj.put("password", "112u");
        obj.put("terminalType", "02");
        String url = "/cardbalance";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"80\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void last10TransactionTest_isOK_whenResponseCodeIs77() throws Exception {
        obj.put("transactionDate", "2020-11-01 14:59:28");
        obj.put("terminalType", "59");
        String url = "/last10transaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"77\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void last10TransactionTest_isOK_whenResponseCodeIs00() throws Exception {
        obj.put("terminalType", "59");
        String url = "/last10transaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"00\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void last10TransactionTest_isOK_whenResponseCodeIs94() throws Exception {
        obj.put("terminalType", "59");
        String url = "/last10transaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"94\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void last10TransactionTest_isOK_whenResponseCodeIs57() throws Exception {
        obj.put("password", "1127");
        obj.put("terminalType", "59");
        String url = "/last10transaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"57\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void last10TransactionTest_isOK_whenResponseCodeIs15() throws Exception {
        obj.put("cardNumber", 1000111111111128L);
        obj.put("terminalType", "59");
        String url = "/last10transaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"15\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void last10TransactionTest_isOK_whenResponseCodeIs12() throws Exception {

        String url = "/last10transaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"12\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void last10TransactionTest_isOK_whenResponseCodeIs80() throws Exception {
        obj.put("password", "112u");
        obj.put("terminalType", "59");
        String url = "/last10transaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"80\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardToCardTest_isOK_whenResponseCodeIs77() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("destinationCardNumber", 1111111111111128L);
        obj.put("terminalType", "59");
        obj.put("transactionDate", "2020-11-01 14:59:28");
        obj.put("amount", 1000);
        String url = "/cardtocard";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"77\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardToCardTest_isOK_whenResponseCodeIs00() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("destinationCardNumber", 1111111111111128L);
        obj.put("terminalType", "59");
        obj.put("amount", 2500);
        String url = "/cardtocard";

        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();

        String expected = "{\"responseCode\":\"00\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardToCardTest_isOK_whenResponseCodeIs94() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("destinationCardNumber", 1111111111111128L);
        obj.put("terminalType", "59");
        obj.put("amount", 2500);

        String url = "/cardtocard";

        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();

        String expected = "{\"responseCode\":\"94\"}";

        JSONAssert.assertEquals(expected, actual, false);

    }

    @Test
    void cardToCardTest_isOK_whenResponseCodeIs57() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("destinationCardNumber", 1111111111111128L);
        obj.put("password", "1127");
        obj.put("terminalType", "59");
        obj.put("amount", 1000);
        String url = "/cardtocard";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"57\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardToCardTest_isOK_whenResponseCodeIs15() throws Exception {
        obj.put("cardNumber", 1000111111111127L);
        obj.put("destinationCardNumber", 1111111111111128L);
        obj.put("terminalType", "59");
        obj.put("amount", 1000);
        String url = "/cardtocard";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"15\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardToCardTest_isOK_whenResponseCodeIs12() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("destinationCardNumber", 1111111111111128L);
        obj.put("amount", 1000);

        String url = "/cardtocard";

        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();

        String expected = "{\"responseCode\":\"12\"}";

        JSONAssert.assertEquals(expected, actual, false);

    }

    @Test
    void cardToCardTest_isOK_whenResponseCodeIs80() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("destinationCardNumber", 1111111111111128L);
        obj.put("password", "11u8");
        obj.put("terminalType", "59");
        obj.put("amount", 1000);
        String url = "/cardtocard";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"80\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void dailyTransactionTest_isOK_whenResponseCodeIs77() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("terminalType", "02");
        obj.put("transactionDate", "2020-11-01 14:59:28");
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        String url = "/dailytransaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"77\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void dailyTransactionTest_isOK_whenResponseCodeIs00() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("terminalType", "02");
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        String url = "/dailytransaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"00\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void dailyTransactionTest_isOK_whenResponseCodeIs94() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("terminalType", "02");
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        String url = "/dailytransaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"94\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void dailyTransactionTest_isOK_whenResponseCodeIs57() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("password", "1127");
        obj.put("terminalType", "02");
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        String url = "/dailytransaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"57\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void dailyTransactionTest_isOK_whenResponseCodeIs15() throws Exception {
        obj.put("cardNumber", 1000111111111127L);
        obj.put("terminalType", "02");
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        String url = "/dailytransaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"15\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void dailyTransactionTest_isOK_whenResponseCodeIs12() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        String url = "/dailytransaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"12\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void dailyTransactionTest_isOK_whenResponseCodeIs80() throws Exception {
        obj.put("cardNumber", 1111111111111127L);
        obj.put("password", "112u");
        obj.put("terminalType", "02");
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        String url = "/dailytransaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"80\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void cardToCardTest_isOK_whenResponseCodeIs51() throws Exception {
        obj.put("cardNumber", 1111111111111125L);
        obj.put("destinationCardNumber", 1111111111111128L);
        obj.put("terminalType", "59");
        obj.put("amount", 100000);
        String url = "/cardtocard";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"51\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    void dailyTransactionTest_isOK_whenResponseCodeIs51() throws Exception {
        obj.put("cardNumber", 1111111111111126L);
        obj.put("terminalType", "02");
        obj.put("startDate", startDate);
        obj.put("endDate", endDate);
        String url = "/dailytransaction";
        MvcResult result = mockMvc.perform(
                post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(obj))
        ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String actual = result.getResponse().getContentAsString();
        String expected = "{\"responseCode\":\"51\"}";

        JSONAssert.assertEquals(expected, actual, false);
    }
}
