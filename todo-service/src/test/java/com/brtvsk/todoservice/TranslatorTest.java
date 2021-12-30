package com.brtvsk.todoservice;

import com.brtvsk.todoservice.i18n.Translator;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.invokeMethod;

public class TranslatorTest {

    private final ResourceBundleMessageSource resourceBundleMessageSource = mock(ResourceBundleMessageSource.class);
    private final Translator translator = new Translator(resourceBundleMessageSource);

    @Test
    void shouldToLocale() {
        String testCode = "mock.code";
        List<String> testArgs = List.of("mockId");
        String mockMsg = "mock";

        MockitoAnnotations.openMocks(this);
        when(invokeMethod(resourceBundleMessageSource, "getMessageInternal", anyString(), any(Object[].class), any(Locale.class)))
                .thenReturn(mockMsg);

        String msg = translator.toLocale(testCode, testArgs);

        assertThat(msg).isEqualTo(mockMsg);
    }

}
