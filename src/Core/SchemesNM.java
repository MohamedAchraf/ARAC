package Core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class SchemesNM {

    /**
     *
     * @param scheme
     * @return Correspendant regular expression
     */
    public String buildRegex(String scheme) {

        String regex = "";

        regex += ("^(بال|ال|بال|وبال|فبال|بال|وبال|فبال|كال|وكال|فكال|كال|وكال|فكال|ب|ك|ل|أ|ا|ت|م|ال|ال|لل|وب|وك|ول|وأ|وال|وال|ولل|فب|فك|فل|فأ|فال|فال|أ|ت|ن|ي|ل|وأ|وت|لت|ون|وي|فأ|فت|فن|في|يست|سأ|سن|سي|ست|أ|ت|ن|ي|سأ|سن|سي|ست|ف|و|فلأ|فلن|فلي|فلت|فسأ|فسن|فسي|في|فست|أوأ|أون|أوي|أوت|أت|أفأ|أفن|أفي|أفت|ا|أسأ|أسن|أسي|أست|ولأ|ا|وا|فا|ولن|ولي|ولت|يست|وسأ|وسن|واست|وسي|وست|فلل)?");
        for (char c : scheme.toCharArray()) {
            if (c == 'ف' || c == 'ع' || c == 'ل') {
                regex += "([ةئءؤإاآأابتثجحخدذرزسشصضطظعغفقكلمنهوي])";
            } else {
                regex += "([" + c + "])";
            }
        }

        regex += "((ه|ها|هما|هم|هم|هم|هن|ك|كما|كم|كن|نا|ي|ين|ان|ة|ة|ة|ة|ة|ة|ا|اء|اء|اء|ات|ه|ه|ه|ني|ها|هما|هم|هن|ك|كما|كم|كن|ناه|ناها|ناهما|ناهم|ناهن|ناك|ناكما|ناكم|ناكن|نا|ت|ت|ت|ت|تا|تما|تم|تن|ن|ن|ا|ون|ي|ين|ان|وا|وا|وه|ا|ونكم|ات)?)";
        regex = regex + "$";

        return regex;
    }

    /**
     * Match schemes and prepositions using an element of the scheme list
     *
     * @param word
     * @param scheme
     * @param _f
     * @param _a
     * @param _l
     * @param tripleRootsList
     * @return
     */
    public String schemesMatcher(String word,
            ArrayList<String> scheme,
            ArrayList<Integer> _f,
            ArrayList<Integer> _a,
            ArrayList<Integer> _l,
            ArrayList<String> tripleRootsList) {
        //======================================================================
        //                       Prepositions Matcher
        //======================================================================
        Matcher w;
        //----------------------------------------------------------------------
        Pattern iste2nef = Pattern.compile("^(ف|و)?"
                + "(حتى|ثم|أو|أم|أما|إما|بل|لأن|إلا|غير أن|إذا|إذ|إذن|أي)"
                + "((ه|نا|ها|هما|هم|هم|هن|ك|كما|كم|كم|كن)?)$");

        w = iste2nef.matcher(word);
        while (w.find()) {
            return "_استأناف_";
        }

        //----------------------------------------------------------------------
        Pattern jarr = Pattern.compile("^(ف|و)?"
                + "(من|من|إلى|إلى|إلي|عن|عن|على|علي|في|مما|رب)$");

        w = jarr.matcher(word);
        while (w.find()) {
            return "_جر_";
        }

        //----------------------------------------------------------------------
        Pattern jarrWmajrour = Pattern.compile("^(ف|و)?"
                + "(من|من|إلى|إلي|عن|عن|على|علي|في|مما|كذل|ب|ل|رب)"
                + "(ه|ه|نا|ها|هما|هما|هم|هم|هم|هن|ك|ك|كما|كم|كم|كن)$");

        w = jarrWmajrour.matcher(word);
        while (w.find()) {
            return "_جارومجرور_";
        }

//----------------------------------------------------------------------
        Pattern istifham = Pattern.compile("^(ف|و)?"
                + "(هل|من|ماذا|إيان|أي|كيف|أين|متى|كم|أنى|علام|عم|مم|إلام|أمن|فيم)"
                + "((هما|هم|هم|هن|كما|كم|كم|كن)?)$");

        w = istifham.matcher(word);
        while (w.find()) {
            return "_استفهام_";
        }

        //----------------------------------------------------------------------
        Pattern dhamirRaf3 = Pattern.compile("^(ف|و)?"
                + "(أنا|أنت|هو|هي|نحن|أنتما|هما|أنتم|أنتن|هم|هم|هن)$");

        w = dhamirRaf3.matcher(word);
        while (w.find()) {
            return "_ضميررفع_";
        }

        //----------------------------------------------------------------------
        Pattern dhamirNasb = Pattern.compile("^(ف|و)?"
                + "(إيا)"
                + "((ه|نا|ي|ها|هما|هم|هم|هن|ك|كما|كم|كم|كن))$");

        w = dhamirNasb.matcher(word);
        while (w.find()) {
            return "_ضميرنصب_";
        }

        //----------------------------------------------------------------------
        Pattern ismIchara = Pattern.compile("^(ف|و|ل)?"
                + "(هذا|هذه|هذان|هاذا|هاذه|هاذان|هذين|هاتان|هاتين|هؤلاء|هنا|ذاك|ذلك|ذالكم|ذلكم|ذالك|ذانك|ذينك|تلك|تلكم|تانك|تينك|أولاء|أولئك|أولائك|هناك|هنالك|ثم|ثمة)"
                + "((ه|نا|ها|هما|هم|هم|هن|ك|كما|كم|كم|كن)?)$");

        w = ismIchara.matcher(word);
        while (w.find()) {
            return "_اسمإشارة_";
        }

        //----------------------------------------------------------------------
        Pattern ismMawsoul = Pattern.compile("^(ف|و|ل)?"
                + "(الذي|اللذان|اللذين|الذين|التي|اللتان|اللتين|اللاتي|اللائي|اللائي|اللواتي)$");

        w = ismMawsoul.matcher(word);
        while (w.find()) {
            return "_اسمموصول_";
        }

        //----------------------------------------------------------------------
        Pattern nidaa = Pattern.compile("^(يا|ويا|فيا|أيها|أيا|أي)$");

        w = nidaa.matcher(word);
        while (w.find()) {
            return "_نداء_";
        }

        //----------------------------------------------------------------------
        Pattern chart = Pattern.compile("^(ف|و)?"
                + "(إن|لو|لن|ما|مهما|أي|كيفما|إذا|إنما|متى|حيثما|أينما|أنى)$");

        w = chart.matcher(word);
        while (w.find()) {
            return "_شرط_";
        }

        //----------------------------------------------------------------------
        Pattern jazm = Pattern.compile("^(ف|و)?"
                + "(لم|إلا|لما|غير|غير|غير|لا)$");

        w = jazm.matcher(word);
        while (w.find()) {
            return "_جزم_";
        }

        //----------------------------------------------------------------------
        Pattern ismAlJalala = Pattern.compile("^(ف|ب|و)?"
                + "(الله|لله|الله|الله)$");

        w = ismAlJalala.matcher(word);
        while (w.find()) {
            return "لفظجلالة";

        }

        //----------------------------------------------------------------------
        Pattern istithnaa = Pattern.compile("^(ف|و|ل)?"
                + "(إن|أن|لكن|لاكن|كأن|ليت|لعل)"
                + "((ه|نا|ا|ها|هما|هم|هم|هن|ك|كما|كم|كم|كن|ما)?)$");

        w = istithnaa.matcher(word);
        while (w.find()) {
            return "_ناسخ_";
        }

        //----------------------------------------------------------------------
        Pattern ism = Pattern.compile("^(ف|ل|و)?"
                + "(أبو|أبي|أبا|أخو|أخي|أخا|حمو|حمي|حما|ذو|ذي|بن)"
                + "((ه|ه|نا|ها|هما|هما|هم|هم|هم|هن|ك|كما|كم|كم|كن)?)$");

        w = ism.matcher(word);
        while (w.find()) {
            return "_اسم_";
        }

        //----------------------------------------------------------------------
        Pattern ta79i9 = Pattern.compile("^(ف|و|ول|فل)?"
                + "(سوف|قد)$");

        w = ta79i9.matcher(word);
        while (w.find()) {
            return "_تحقيق_";
        }


        //----------------------------------------------------------------------
        Pattern dharf = Pattern.compile("فوق|تحت|يمين|يسار|أمام|خلف|جانب|بين|مكان|ناحية|وسط|خلال|تجاه|إزاء|حذاء|قرب|حول|شرق|غرب|جنوب|شمال|أين|أنى|ثم|حيث|هنا|هناك|كذا|عند|لدى|لدن|ذات|بين|قبل|بعد|أول|فوق|تحت|يمين|يسار|أمام|خلف|جانب|بين|مكان|ناحية|وسط|خلال|تجاه|إزاء|حذاء|قرب|حول|شرق|غرب|جنوب|شمال|أين|أنى|ثم|حيث|هنا|هناك|كذا|عند|لدى|لدن|ذات|بين|قبل|بعد|أول|مع|مع");

        w = dharf.matcher(word);
        while (w.find()) {
            return "_ظرف_";
        }

        //----------------------------------------------------------------------		
        Pattern inna = Pattern.compile("([\\p{Punct}ۖۖۖ؛،ۚۛۙ؟ۗ\\s+؛])");

        w = inna.matcher(word);
        while (w.find()) {
            return "_تنقيط_";
        }

        //======================================================================
        //                       Schemes Matcher
        //======================================================================    
        for (int j = scheme.size()-1; j >=0 ; j--) {                
            if (scheme.get(j).length() > word.length()) break;
                Boolean FFound = Boolean.FALSE, AFound = Boolean.FALSE, LFound = Boolean.FALSE;
                Pattern patternBuild1 = Pattern.compile(buildRegex(scheme.get(j)));
                Matcher a;
                a = patternBuild1.matcher(word);
                while (a.find()) {
                    String root = a.group(_f.get(j)) + a.group(_a.get(j)) + a.group(_l.get(j)),
                            wordPattern = "";
                    for (int i = 1; i < a.groupCount(); ++i) {
                        if (a.group(i) != null) {
                            if (i == _f.get(j)) {
                                wordPattern += "ف";
                                FFound = Boolean.TRUE;
                            } else if (i == _a.get(j) && FFound) {
                                wordPattern += "ع";
                                AFound = Boolean.TRUE;
                            } else if (i == _l.get(j) && FFound && AFound) {
                                wordPattern += "ل";
                                LFound = Boolean.TRUE;

                            } else {
                                wordPattern += a.group(i);
                            }
                        }
                    }
                    if (FFound && AFound && LFound
                            && tripleRootsList.contains(root)) {
                        return wordPattern;
                    }
                }
        }
        return "_مجهول_";
    }
}