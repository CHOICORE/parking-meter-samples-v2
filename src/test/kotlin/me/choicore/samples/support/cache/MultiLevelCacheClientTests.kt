// package me.choicore.samples.support.cache
//
// import me.choicore.samples.core.TypeReference
// import me.choicore.samples.support.cache.CacheLevel.L2
// import org.assertj.core.api.Assertions.assertThat
// import org.springframework.boot.test.context.SpringBootTest
// import org.springframework.test.context.TestConstructor
// import kotlin.test.Test
//
// @SpringBootTest
// @TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
// class MultiLevelCacheClientTests(
//    private val cacheClient: MultiLevelCacheClient,
// ) {
//    @Test
//    fun t1() {
//        val namespace = Namespace("metering-rules")
//        val key = CacheKey("1")
//        val value = "value"
//        cacheClient.put(namespace, key, value)
//        val get = cacheClient.get(namespace, key, String::class.java)
//
//        val missingKey = CacheKey("new-key")
//        val aside =
//            cacheClient.aside(namespace, missingKey, String::class.java, level = L2) {
//                "aside"
//            }
//
//        cacheClient.aside(namespace, missingKey, object : TypeReference<String>() {}, level = L2) {
//            "aside"
//        }
//        assertThat(aside).isEqualTo("aside")
//
//        val get1 = cacheClient.get(namespace, key, String::class.java, level = L2)
//    }
// }
