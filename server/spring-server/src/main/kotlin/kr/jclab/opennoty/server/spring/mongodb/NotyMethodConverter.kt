package kr.jclab.opennoty.server.spring.mongodb

import kr.jclab.opennoty.model.NotyMethod
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

class NotyMethodConverter {
    @WritingConverter
    class ToStringConverter : Converter<NotyMethod, String> {
        override fun convert(source: NotyMethod): String {
            return source.value
        }
    }

    @ReadingConverter
    class FromStringConverter : Converter<String, NotyMethod> {
        override fun convert(source: String): NotyMethod? {
            return NotyMethod.findByValue(source)
        }
    }
}