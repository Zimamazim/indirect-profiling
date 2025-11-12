package org.jetbrains.kotlinx.stdlibbenchmarks

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

import kotlin.io.path.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@State(Scope.Benchmark)
class SerializationBenchmark {

    private var text = Path("src/commonMain/resources/twitter.json").readText()

    @Benchmark
    fun encodeDecode() = Json.encodeToString(Twitter.serializer(), Json.decodeFromString(Twitter.serializer(), text))
}

@Serializable
data class Twitter(
    val search_metadata: SearchMetadata,
    val statuses: List<Status>
)

@Serializable
data class SearchMetadata(
    val completed_in: Double,
    val count: Int,
    val max_id: Long,
    val max_id_str: String,
    val next_results: String,
    val query: String,
    val refresh_url: String,
    val since_id: Int,
    val since_id_str: String
)

@Serializable
data class Status(
    val created_at: String,
    val entities: Entities,
    val favorited: Boolean,
    val id: Long,
    val id_str: String,
    val possibly_sensitive: Boolean = false,
    val retweet_count: Int,
    val retweeted: Boolean,
    val source: String,
    val text: String,
    val truncated: Boolean,
    val user: User
)

@Serializable
data class Entities(
    val hashtags: List<Hashtag>,
    val media: List<Media> = emptyList(),
    val urls: List<Url>,
    val user_mentions: List<UserMention>
)

@Serializable
data class User(
    val contributors_enabled: Boolean,
    val created_at: String,
    val default_profile: Boolean,
    val default_profile_image: Boolean,
    val description: String,
    val favourites_count: Int,
    val follow_request_sent: Boolean,
    val followers_count: Int,
    val following: Boolean,
    val friends_count: Int,
    val geo_enabled: Boolean,
    val id: Int,
    val id_str: String,
    val is_translator: Boolean,
    val lang: String,
    val listed_count: Int,
    val location: String,
    val name: String,
    val notifications: Boolean,
    val profile_background_color: String,
    val profile_background_image_url: String,
    val profile_background_image_url_https: String,
    val profile_background_tile: Boolean,
    val profile_image_url: String,
    val profile_image_url_https: String,
    val profile_link_color: String,
    val profile_sidebar_border_color: String,
    val profile_sidebar_fill_color: String,
    val profile_text_color: String,
    val profile_use_background_image: Boolean,
    val url: String? = null,
    val `protected`: Boolean,
    val screen_name: String,
    val show_all_inline_media: Boolean,
    val statuses_count: Int,
    val time_zone: String?,
    val utc_offset: Int?,
    val verified: Boolean
)

@Serializable
data class Hashtag(
    val indices: List<Int>,
    val text: String
)

@Serializable
data class Media(
    val display_url: String,
    val expanded_url: String,
    val id: Long,
    val id_str: String,
    val indices: List<Int>,
    val media_url: String,
    val media_url_https: String,
    val sizes: Sizes,
    val type: String,
    val url: String
)

@Serializable
data class Url(
    val display_url: String,
    val expanded_url: String,
    val indices: List<Int>,
    val url: String
)

@Serializable
data class UserMention(
    val id: Int,
    val id_str: String,
    val indices: List<Int>,
    val name: String,
    val screen_name: String
)

@Serializable
data class Sizes(
    val large: Large,
    val medium: Medium,
    val small: Small,
    val thumb: Thumb
)

@Serializable
data class Large(
    val h: Int,
    val resize: String,
    val w: Int
)

@Serializable
data class Medium(
    val h: Int,
    val resize: String,
    val w: Int
)

@Serializable
data class Small(
    val h: Int,
    val resize: String,
    val w: Int
)

@Serializable
data class Thumb(
    val h: Int,
    val resize: String,
    val w: Int
)


//
//@State(Scope.Benchmark)
//class Base64Benchmark {
//
//    private var text = "This reference is designed for you to easily learn Kotlin in a matter of hours. " +
//                "Start with the basic syntax, then proceed to more advanced topics. While reading, " +
//                "you can try out the examples in the online IDE. " +
//                "Once you get an idea of what Kotlin looks like, " +
//                "try solving some Kotlin Koans - interactive programming exercises. " +
//                "If you are not sure how to solve a Koan, " +
//                "or you're looking for a more elegant solution, check out Kotlin idioms."
//    private var encoded_text = "VGhpcyByZWZlcmVuY2UgaXMgZGVzaWduZWQgZm9yIHlvdSB0byBlYXNpbHkgbGVhcm4gS290bGluIG" +
//            "luIGEgbWF0dGVyIG9mIGhvdXJzLiBTdGFydCB3aXRoIHRoZSBiYXNpYyBzeW50YXgsIHRoZW4gcHJvY2VlZCB0by" +
//            "Btb3JlIGFkdmFuY2VkIHRvcGljcy4gV2hpbGUgcmVhZGluZywgeW91IGNhbiB0cnkgb3V0IHRoZSBleGFtcGxlcy" +
//            "BpbiB0aGUgb25saW5lIElERS4gT25jZSB5b3UgZ2V0IGFuIGlkZWEgb2Ygd2hhdCBLb3RsaW4gbG9va3MgbGlrZS" +
//            "wgdHJ5IHNvbHZpbmcgc29tZSBLb3RsaW4gS29hbnMgLSBpbnRlcmFjdGl2ZSBwcm9ncmFtbWluZyBleGVyY2lzZX" +
//            "MuIElmIHlvdSBhcmUgbm90IHN1cmUgaG93IHRvIHNvbHZlIGEgS29hbiwgb3IgeW91J3JlIGxvb2tpbmcgZm9yIG" +
//            "EgbW9yZSBlbGVnYW50IHNvbHV0aW9uLCBjaGVjayBvdXQgS290bGluIGlkaW9tcy4="
//
//    @Benchmark
//    fun encodeDecode() = text.encodeBase64().decodeBase64String()
//}