package jmh_is_stupid_and_cant_use_default_package

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State

import io.ktor.util.encodeBase64
import io.ktor.util.decodeBase64String

@State(Scope.Benchmark)
class Base64Benchmark {

    private var text = "This reference is designed for you to easily learn Kotlin in a matter of hours. " +
                "Start with the basic syntax, then proceed to more advanced topics. While reading, " +
                "you can try out the examples in the online IDE. " +
                "Once you get an idea of what Kotlin looks like, " +
                "try solving some Kotlin Koans - interactive programming exercises. " +
                "If you are not sure how to solve a Koan, " +
                "or you're looking for a more elegant solution, check out Kotlin idioms."
    private var encoded_text = "VGhpcyByZWZlcmVuY2UgaXMgZGVzaWduZWQgZm9yIHlvdSB0byBlYXNpbHkgbGVhcm4gS290bGluIG" +
            "luIGEgbWF0dGVyIG9mIGhvdXJzLiBTdGFydCB3aXRoIHRoZSBiYXNpYyBzeW50YXgsIHRoZW4gcHJvY2VlZCB0by" +
            "Btb3JlIGFkdmFuY2VkIHRvcGljcy4gV2hpbGUgcmVhZGluZywgeW91IGNhbiB0cnkgb3V0IHRoZSBleGFtcGxlcy" +
            "BpbiB0aGUgb25saW5lIElERS4gT25jZSB5b3UgZ2V0IGFuIGlkZWEgb2Ygd2hhdCBLb3RsaW4gbG9va3MgbGlrZS" +
            "wgdHJ5IHNvbHZpbmcgc29tZSBLb3RsaW4gS29hbnMgLSBpbnRlcmFjdGl2ZSBwcm9ncmFtbWluZyBleGVyY2lzZX" +
            "MuIElmIHlvdSBhcmUgbm90IHN1cmUgaG93IHRvIHNvbHZlIGEgS29hbiwgb3IgeW91J3JlIGxvb2tpbmcgZm9yIG" +
            "EgbW9yZSBlbGVnYW50IHNvbHV0aW9uLCBjaGVjayBvdXQgS290bGluIGlkaW9tcy4="

    @Benchmark
    fun encodeDecode() = text.encodeBase64().decodeBase64String()
}
