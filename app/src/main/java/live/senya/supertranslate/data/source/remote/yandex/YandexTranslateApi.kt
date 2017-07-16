package live.senya.supertranslate.data.source.remote.yandex

import io.reactivex.Maybe
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface YandexTranslateApi {

    @FormUrlEncoded
    @POST("tr.json/translate")
    fun translate(@Field("key") key: String,
                  @Field("text") text: String,
                  @Field("lang") lang: String): Maybe<YandexTranslateResponse>

}