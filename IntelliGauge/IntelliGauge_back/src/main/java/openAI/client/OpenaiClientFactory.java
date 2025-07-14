package openAI.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import common.accountPool.AccountPoolManager;
import common.entity.account.ChatSdkAccount;
import common.entity.account.ClientParam;
import common.service.AccountCacheManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WuHao
 * @since 2023/11/29 16:41
 */
public class OpenaiClientFactory implements AccountCacheManager {

    public static final ConcurrentHashMap<String, OpenAiService> serviceCacheMap = new ConcurrentHashMap<>();

    public static OpenAiService getService(ChatSdkAccount account) {
        String apiToken = account.getApiToken();

        ClientParam clientParam = account.getClientParam();
        String baseUrl = clientParam.getBaseUrl();

        OpenAiService openAiService = serviceCacheMap.get(baseUrl);

        if (Objects.isNull(openAiService)) {
            OkHttpClient client = AccountPoolManager.getClient(account.getClientParam(), request -> {
                request.header("Authorization", "Bearer " + apiToken);
            });

            ObjectMapper mapper = OpenAiService.defaultObjectMapper();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create(mapper))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            openAiService = new OpenAiService(retrofit.create(OpenAiApi.class), client.dispatcher().executorService());
        }

        return openAiService;
    }

    @Override
    public void cacheRemove(String baseUrl) {
        serviceCacheMap.remove(baseUrl);
    }

    @Override
    public void cacheAll() {
        serviceCacheMap.clear();
    }
}
