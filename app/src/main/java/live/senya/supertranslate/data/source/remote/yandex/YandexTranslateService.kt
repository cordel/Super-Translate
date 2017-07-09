package live.senya.supertranslate.data.source.remote.yandex

import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface YandexTranslateService {

    @FormUrlEncoded
    @POST("tr.json/translate")
    fun translate(@Field("key") key: String,
                  @Field("text") text: String,
                  @Field("lang") lang: String): Single<YandexTranslateResponse>

}