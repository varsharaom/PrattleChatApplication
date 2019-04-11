import com.ibm.watson.developer_cloud.language_translator.v3.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.developer_cloud.language_translator.v3.model.TranslationResult;
import com.ibm.watson.developer_cloud.service.security.IamOptions;

public class Translator {

    private String sourceLanguage;
    private String targetLanguage;

    private String version;
    private String apiKey;
    private String url;
    private String modelId;

    public Translator(String sourceLanguage, String targetLanguage) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.version = "2018-05-01";
        this.apiKey = "t2gW6j-LmyB8ajy4Jadd4U12_zwUlDw0TQ6XgZ_q2Hfd";
        this.url = "https://gateway.watsonplatform.net/language-translator/api\n";
        setModelId();
    }

    private void setModelId() {
        this.modelId = this.sourceLanguage + "-" + this.targetLanguage;
    }


    public String translate(String text) {
        TranslationResult translatedResult = ibmTranslate(text);
        return extractTranslatedText(translatedResult);
    }

    private String extractTranslatedText(TranslationResult translatedResult) {
        return translatedResult
                .getTranslations()
                .get(0)
                .getTranslationOutput();
    }

    private TranslationResult ibmTranslate(String text) {
        IamOptions options = new IamOptions.Builder()
                .apiKey(apiKey)
                .build();
        LanguageTranslator languageTranslator = new LanguageTranslator(version, options);
        languageTranslator.setEndPoint(url);

        TranslateOptions translateOptions = new TranslateOptions.Builder()
                .addText(text)
                .modelId(modelId)
                .build();

        return languageTranslator.translate(translateOptions)
                .execute();

    }

    public static void main(String[] args) {
        Translator obj = new Translator("en", "es");
        String translated = obj.translate("Hello world. How are you?");
        System.out.println(translated);
    }
}
