package Core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mohamed Achraf BEN MOHAMED<mohamedachraf@wanadoo.fr>
 */
public class SchemesForConversion {
    
    

    /**
     *
     * @param scheme
     * @return Correspondent regular expression
     */
    public String buildRegex(String scheme) {

        String regex = "";

        regex += ("^(بِال|الِ|بِالِ|وَبِال|فَبِال|بِالْ|الْ|وَبِالْ|فَبِالْ|كَال|وَكَال|فَكَال|كَالْ|وَكَالْ|فَكَالْ|بِ|كَ|لِ|أَ|اُ|تَ|مُ|ال|الْ|لِلْ|وَبِ|وَكَ|وَلِ|وَأَ|وَال|وَالْ|وَلِلْ|فَبِ|فَكَ|فَلِ|فَأَ|فَال|فَالْ|أَ|تَ|نَ|يَ|لَ|وَأَ|وَتَ|لِتَ|وَنَ|وَيَ|فَأَ|فَتَ|فَنَ|فَيَ|يَسْتَ|سَ|سَأَ|سَنَ|سَيَ|سَتَ|أُ|تُ|نُ|يُ|سُأُ|سُنُ|سُيُ|سُتُ|فَ|وَ|فَلْأَ|فَلْنَ|فَلْيَ|فَلْتَ|فَسَأَ|فَسَنَ|فَسَيَ|فَيَ|فَسَتَ|أَوَأَ|أَوَنَ|أَوَيَ|أَوَتَ|أَتَ|أَفَأَ|أَفَنَ|أَفَيَ|أَفَتَ|ا|أَسَأَ|أَسَنَ|أَسَيَ|أَسَتَ|وَلْأَ|ا|وَا|فَا|وَلْنَ|وَلْيَ|وَلْتَ|يَسْتَ|وَسَأَ|وَسَنَ|وَاسْتَ|وَسَيَ|وَسَتَ|فَلِلْ)?");
        for (char c : scheme.toCharArray()) {
            if (c == 'ف' || c == 'ع' || c == 'ل') {
                regex += "([ةئءؤإاآأابتثجحخدذرزسشصضطظعغفقكلمنهوي])";
         } else if (c == 'َ' || c == 'ً' || c == 'ُ' || c == 'ٌ' || c == 'ِ' || c == 'ٍ' || c == 'ْ' || c == 'ّ') {
               regex += "([ًٌٍَُِّْ]?)";
            } else {
                regex += "([" + c + "])";
            }
        }
        regex += "([ًٌٍَُِّْ]?)";
        regex += "((هُ|هَا|هُمَا|هِمْ|هَمْ|هُمْ|هِنَّ|كَ|كُمَا|كُمْ|كُنَّ|نَا|ي|ينَ|انِ|ةٌ|ةٍ|ةً|ةَ|ةُ|ةِ|ا|اءِ|اءُ|اءَ|اتٍ|هُ|هِ|هَ|نِي|هَا|هُمَا|هُمْ|هُنَّ|كَ|كُمَا|كُمْ|كُنَّ|نَاهُ|نَاهَا|نَاهُمَا|نَاهُمْ|نَاهُنَّ|نَاكَ|نَاكُمَا|نَاكُمْ|نَاكُنَّ|نَا|تُ|تِ|تَ|تْ|تَا|تُمَا|تُمْ|تُنَّ|نَ|نُ|ا|ونَ|ي|ينَ|انِ|وُا|وا|وهُ|ا|ونَكُمْ|اتٌ)?)";
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
            ArrayList<String> tripleRootsList,
            Boolean WithSplit) {
        
        String Enc = "", Pro = "";
        //======================================================================
        //                       Pre-Traitement
        //======================================================================
        String pattern2 = "(ـ|  )";
        word = word.replaceAll(pattern2, "");
        word = word.replaceAll("َّ", "َّ");
        word = word.replaceAll("ِّ", "ِّ");
        word = word.replaceAll("ُّ", "ُّ");
        word = word.replaceAll("ٰ", "ا");
        word = word.replaceAll("ىا", "ى");        

        //======================================================================
        //                       Prepositions Matcher
        //======================================================================
        Matcher w;
        //----------------------------------------------------------------------
        Pattern iste2nef = Pattern.compile("^(فَ|وَ)?"
                + "(حَتَّى|ثُمَّ|أَوْ|أَمْ|أَمَا|إِمَّا|بَلْ|لَأَنَّ|إِلَّا|غَيْرَ أَنَّ|إذاً|إِذْ|إِذَنْ)"
                + "((هُ|نَا|هَا|هُمَا|هُمْ|هِمُ|هُنَّ|كَ|كُمَا|كُمْ|كُمُ|كُنَّ)?)$");

        w = iste2nef.matcher(word);
        while (w.find()) {
            return "_استأناف_";
        }

        //----------------------------------------------------------------------
        Pattern jarr = Pattern.compile("^(فَ|وَ)?"
                + "(مِنْ|مِنَ|إلَى|إِلَى|إِلَيْ|عَنْ|عَنِ|عَلَى|عَلَيْ|فِي|مِمَّا|رُبَّ)$");

        w = jarr.matcher(word);
        Enc = "";Pro = "";
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            return Enc + " _جار_";
        }

        //----------------------------------------------------------------------
        Pattern jarrWmajrour = Pattern.compile("^(فَ|وَ)?"
                + "(مِنْ|مِنَ|إِلَى|إِلَيْ|عَنْ|عَنِ|عَلَى|عَلَيْ|فِي|مِمَّا|كَذَلِ|بِ|لَ|رُبَّ)"
                + "(هُ|هِ|نَا|هَا|هُمَا|هِمَا|هُمْ|هِمُ|هِمْ|هُنَّ|كَ|كِ|كُمَا|كُمْ|كُمُ|كُنَّ)$");

        w = jarrWmajrour.matcher(word);
         Enc = "";
         Pro = "";
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            if(w.group(3) != null) Pro = w.group(3);
            
            return Enc+ " _جار_ " + Pro;            
        }

        //----------------------------------------------------------------------
        Pattern istifham = Pattern.compile("^(فَ|وَ)?"
                    + "(هَلْ|مَنْ|بِمَ|مَاذَا|إِيَّانَ|أَيُّ|كَيْفَ|لِمَ|أَيْنَ|مَتَى|كَمْ|بِكَمْ|أَنَّى|عَلَامَ|عَمَّ|مِمَّ|إِلَامَ|أَمَّنْ|فِيمَ)"
                + "((هُمَا|هُمْ|هِمُ|هُنَّ|كُمَا|كُمْ|كُمُ|كُنَّ)?)$");

        w = istifham.matcher(word);
        Enc = "";
        Pro = "";
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            if(w.group(3) != null) Pro = w.group(3);
            
            return Enc + " _استفهام_ " + Pro;
        }

        //----------------------------------------------------------------------
        Pattern dhamirRaf3 = Pattern.compile("^(فَ|وَ)?"
                + "(أَنَا|أَنْتَ|هُوَ|هِيَ|نَحْنُ|أَنْتُمَا|هُمَا|أَنْتُمْ|أَنْتُنَّ|هُمْ|هُمُ|هُنّ)$");

        w = dhamirRaf3.matcher(word);
        Enc = "";
        Pro = "";
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);            
            return Enc + " _ضميررفع_";
        }

        //----------------------------------------------------------------------
        Pattern dhamirNasb = Pattern.compile("^(فَ|وَ)?"
                + "(إِيَّا)"
                + "((هُ|نَا|يَ|هَا|هُمَا|هُمْ|هِمُ|هُنَّ|كَ|كُمَا|كُمْ|كُمُ|كُنَّ))$");

        w = dhamirNasb.matcher(word);
        Enc = "";
        Pro = "";
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            if(w.group(3) != null) Pro = w.group(3);
            
            return Enc + " _ضميرنصب_ " + Pro;
        }

        //----------------------------------------------------------------------
        Pattern ismIchara = Pattern.compile("^(فَ|وَ|لِ)?"
                + "(هَذَا|هَذِهِ|هَذَانِ|هَاذَا|هَاذِهِ|هَاذَانِ|هَذَيْنِ|هَاتَانِ|هَاتَيْنِ|هَؤُلَاءِ|هُنَا|ذَاكَ|ذَلِكَ|ذَالِكُمْ|ذَلِكُمْ|ذَالِكَ|ذَانِكَ|ذَيْنِكَ|تِلْكَ|تِلْكُمْ|تَانِكَ|تَيْنِكَ|أُولَاءِ|أُولَئِكَ|أُولَائِكَ|هُنَاكَ|هُنَالِكَ|ثَمَّ|ثَمَّةَ)"
                + "((هُ|نَا|هَا|هُمَا|هُمْ|هِمُ|هُنَّ|كَ|كُمَا|كُمْ|كُمُ|كُنَّ)?)$");

        w = ismIchara.matcher(word);
        Enc = "";
        Pro = "";
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            if(w.group(3) != null) Pro = w.group(3);
            
            return Pro + " _اسمإشارة_ " + Enc;
        }

        //----------------------------------------------------------------------
        Pattern ismMawsoul = Pattern.compile("^(فَ|وَ|لِ)?"
                + "(الَّذِي|الْلَذَانِ|الْلَذَيْنِ|الَّذِينَ|الَّتِي|الْلَتَانِ|الْلَتَيْنِ|الْلَاتِي|اللَّائِي|الْلَائِي|الْلَوَاتِي)$");

        w = ismMawsoul.matcher(word);
        while (w.find()) {
            return "_اسمموصول_";
        }
        
             //----------------------------------------------------------------------
        Pattern harf3atf = Pattern.compile("^(فَ|وَ)?"
                + "(ثُمَّ|أَوْ|أَمْ|لَكِنْ|بَلْ|حَتَّى)$");

        w = harf3atf.matcher(word);
        while (w.find()) {
            return "_حرفعطف_";
        }

        //----------------------------------------------------------------------
        Pattern nidaa = Pattern.compile("^(يَا|وَيَا|فَيَا|أَيُّهَا|أَيَا|أَيْ)$");

        w = nidaa.matcher(word);
        while (w.find()) {
            return "_نداء_";
        }

        //----------------------------------------------------------------------
        Pattern chart = Pattern.compile("^(فَ|وَ)?"
                + "(إِنْ|لَوْ|لَنْ|مَا|مَهْمَا|أَيُّ|كَيْفَمَا|إِذَا|إنَّمَا|مَتَى|حَيْثُمَا|أَيْنَمَا|أَنَّى)$");

        w = chart.matcher(word);
        Enc = "";        
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1); 
            return Enc + " _شرط_";
        }

        //----------------------------------------------------------------------
        Pattern jazm = Pattern.compile("^(فَ|وَ)?"
                + "(لَمْ|إلَّا|لَمَّا|غَيْرُ|غَيْرِ|غَيْرَ|لَا)$");

        w = jazm.matcher(word);
         Enc = "";        
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1); 
            return Enc +  " _جزم_";
        }

        //----------------------------------------------------------------------
        Pattern ismAlJalala = Pattern.compile("^(فَ|بِ|وَ)?"
                + "(اللَّهِ|لِلَّهِ|اللَّهُ|اللَّهَ)$");

        w = ismAlJalala.matcher(word);
         Enc = "";        
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            
            return Enc +  " لفظجلالة";
        }
//----------------------------------------------------------------------
        Pattern KenaWA5awatouha = Pattern.compile("^(فَ|وَ)?"
                + "(كَانَ|صَارَ|أَصْبَحَ|أَضْحَى|أَمْسَى|ظَلَّ|بَاتَ|لَيْسَ|مَابَرِحَ|مَازَالَ|مَافَتِئَ|مَااِنْفَكَّ|مَادَامَ)$");

        w = KenaWA5awatouha.matcher(word);
         Enc = "";        
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            
            return Enc + " _أخواتكان_";

        }

        //----------------------------------------------------------------------
        Pattern istithnaa = Pattern.compile("^(فَ|وَ|لِ)?"
                + "(إِنَّ|أَنَّ|لَكِنَّ|لَاكِنْ|كَأَنَّ|لَيْتَ|لَعَلَّ)"
                + "((هُ|نَا|ا|هَا|هُمَا|هُمْ|هِمُ|هُنَّ|كَ|كُمَا|كُمْ|كُمُ|كُنَّ|مَا)?)$");

        w = istithnaa.matcher(word);
        Enc = "";
        Pro = "";
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            if(w.group(3) != null) Pro = w.group(3);
            
            return Enc + " _ناسخ_ " + Pro;
        }

        //----------------------------------------------------------------------
        Pattern ism = Pattern.compile("^(فَ|لَ|وَ)?"
                + "(أَبُو|أَبِي|أَبَا|أَخُو|أَخِي|أَخَا|حَمُو|حَمِي|حَمَا|ذُو|ذِي|بْنُ)"
                + "((هُ|هِ|نَا|هَا|هُمَا|هِمَا|هُمْ|هِمُ|هِمْ|هُنَّ|كَ|كُمَا|كُمْ|كُمُ|كُنَّ)?)$");

        w = ism.matcher(word);
        Enc = "";
        Pro = "";
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);
            if(w.group(3) != null) Pro = w.group(3);
            
            return Enc + " _اسم_ " + Pro;
        }

        //----------------------------------------------------------------------
        Pattern ta79i9 = Pattern.compile("^(فَ|وَ|وَلَ|فَلَ)?"
                + "(لَقَدْ|قَدْ)$");

        w = ta79i9.matcher(word);
         Enc = "";       
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);            
            
            return Enc + " _تحقيق_";
        }

      //----------------------------------------------------------------------
        Pattern Isti9bal = Pattern.compile("^(فَ|وَ|وَلَ|فَلَ)?"
                + "(سَوْفَ|سَ)$");

        w = Isti9bal.matcher(word);
         Enc = "";       
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);            
            
            return Enc + " _استقبال_";
        }


      //-------------------------------------------------------------------------------
        Pattern dharf = Pattern.compile("^(فَ|وَ)?"
                + "(فَوْقَ|تَحْتَ|يَمِينَ|يَسَارَ|أَمَامَ|خَلْفَ|جَانِبَ|بَيْنَ|مَكَانَ|نَاحِيَةَ|وَسَطَ|خِلَالَ|تَجَاهَ|إِزَاءَ|مَعَ|حِذَاءَ|قُرْبَ|حَوْلَ|شَرْقَ|غَرْبَ|جَنُوبَ|شَمَالَ|أَيْنَ|أَنَّى|ثَمَّ|حَيْثُ|هُنَا|هُنَاكَ|كَذَا|عِنْدَ|لَدَى|لَدُنْ|ذَاتَ|بَيْنَ|قَبْلَ|بَعْدَ|أَوَّلَ)$");

        w = dharf.matcher(word);
         Enc = "";       
        while (w.find()) {
            if(w.group(1) != null) Enc = w.group(1);            
            
            return Enc + " _ظرف_";
        }


        //----------------------------------------------------------------------		
        Pattern inna = Pattern.compile("([\\p{Punct}ۖۖۖ؛،ۚۛۙ؟ۗ\\s+؛])");

        w = inna.matcher(word);
        while (w.find()) {
            return "_تنقيط_";
        }
  //----------------------------------------------------------------------		
        Pattern Fi3lMoudha3af = Pattern.compile("(^[ةئءؤإاآأابتثجحخدذرزسشصضطظعغفقكلمنهوي][َ][ةئءؤإاآأابتثجحخدذرزسشصضطظعغفقكلمنهوي][َّ]$)");

        w = Fi3lMoudha3af.matcher(word);
        while (w.find()) {
            return "فَعَلَ";
        }

        //======================================================================
        //                       Schemes Matcher
        //======================================================================    
        for (int j = scheme.size()-1; j >=0 ; j--) {                
           if(scheme.get(j).length() > word.length()) break;    
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
                        }else{
                            wordPattern +=  a.group(i) ;
                        }
                    }
                }
                if (FFound && AFound && LFound && tripleRootsList.contains(root)) {
                    
                    // if we dont want to split scheme : [فكلمنا ] --> [ففعلنا ]                    
                    if(WithSplit == Boolean.FALSE) return wordPattern;                                        
                    //----------------------------------------------------------
                    wordPattern = wordPattern.replace(scheme.get(j), " " + scheme.get(j) + " ");                    
                    wordPattern = wordPattern.trim();   
                    //----------------------------------------------------------

                    String  Enclitic1 = " كَ",   Proclictic11   = "_كَ",
                            Enclitic2 = " هُ",   Proclictic22   = "_هُ",
                            Enclitic3 = " كِ",   Proclictic33   = "_كِ",
                            Enclitic4 = " نَا",  Proclictic44   = "_نَا",
                            Enclitic5 = " كُمْ",  Proclictic55   = "_كُمْ",
                            Enclitic6 = " هُمْ",  Proclictic66   = "_هُمْ",
                            Enclitic7 = " هُمَا", Proclictic77   = "_هُمَا",
                            Enclitic8 = " كُمَا", Proclictic88   = "_كُمَا",
                            Enclitic9 = " كُنَّ",  Proclictic99   = "_كُنَّ",
                            Enclitic10 = " هُنَّ", Proclictic1010 = "_هُنَّ",
                            Enclitic11 = " هِ",  Proclictic1111 = "_هِ",
                            Enclitic12 = " نِي",  Proclictic1212 = "_نِي",
                            Enclitic13 = " هَا",  Proclictic1313 = "_هَا",
                            Enclitic14 = " تُ",   Proclictic1414 = "_تُ";
                    
                    //----------------------------------------------------------
                     String Prolictic1 = "أَ ", Prolictic11 = "أَ_",
                            Prolictic2 = "فَ ", Prolictic22 = "فَ_",
                            Prolictic3 = "سَ ", Prolictic33 = "سَ_",
                            Prolictic4 = "لِ ", Prolictic44 = "لِ_",
                            Prolictic5 = "بِ ", Prolictic55 = "بِ_",
                            Prolictic6 = "وَ ", Prolictic66 = "وَ_",
                            Prolictic7 = "كَ ", Prolictic77 = "كَ_",
                            Prolictic8 = "كَال ", Prolictic88 = "كَ_ال",
                            Prolictic9 = "لَ ", Prolictic99 = "لَ_",
                            Prolictic10 = "بِال ", Prolictic1010 = "بِ_ال";
                    //----------------------------------------------------------
                    // eliminate "تاء التّأْنيث"
                    String Taa_1_ = " ةُ", Ta_1_ = "ةُ",
                            Taa_2_ = " ةَ", Ta_2_ = "ةَ",
                            Taa_3_ = " ةِ", Ta_3_ = "ةِ",
                            Taa_4_ = " ةً", Ta_4_ = "ةً",
                            Taa_5_ = " ةٌ", Ta_5_ = "ةٌ",
                            Taa_6_ = " ةٍ", Ta_6_ = "ةٍ";
                    
                    wordPattern = wordPattern.replace(Taa_1_, Ta_1_);
                    wordPattern = wordPattern.replace(Taa_2_, Ta_2_);
                    wordPattern = wordPattern.replace(Taa_3_, Ta_3_); 
                    wordPattern = wordPattern.replace(Taa_4_, Ta_4_);
                    wordPattern = wordPattern.replace(Taa_5_, Ta_5_);
                    wordPattern = wordPattern.replace(Taa_6_, Ta_6_); 
                    //----------------------------------------------------------
                    wordPattern = wordPattern.replace(Enclitic1, Proclictic11);
                    wordPattern = wordPattern.replace(Enclitic2, Proclictic22);
                    wordPattern = wordPattern.replace(Enclitic3, Proclictic33);
                    wordPattern = wordPattern.replace(Enclitic4, Proclictic44);
                    wordPattern = wordPattern.replace(Enclitic5, Proclictic55);
                    wordPattern = wordPattern.replace(Enclitic6, Proclictic66);
                    wordPattern = wordPattern.replace(Enclitic7, Proclictic77);
                    wordPattern = wordPattern.replace(Enclitic8, Proclictic88);
                    wordPattern = wordPattern.replace(Enclitic9, Proclictic99);
                    wordPattern = wordPattern.replace(Enclitic10, Proclictic1010);
                    wordPattern = wordPattern.replace(Enclitic11, Proclictic1111);
                    wordPattern = wordPattern.replace(Enclitic12, Proclictic1212);
                    wordPattern = wordPattern.replace(Enclitic13, Proclictic1313);
                    wordPattern = wordPattern.replace(Enclitic14, Proclictic1414);
                    
                    wordPattern = wordPattern.replace(Prolictic1, Prolictic11);
                    wordPattern = wordPattern.replace(Prolictic2, Prolictic22);
                    wordPattern = wordPattern.replace(Prolictic3, Prolictic33);
                    wordPattern = wordPattern.replace(Prolictic4, Prolictic44);
                    wordPattern = wordPattern.replace(Prolictic5, Prolictic55);
                    wordPattern = wordPattern.replace(Prolictic6, Prolictic66);
                    wordPattern = wordPattern.replace(Prolictic7, Prolictic77);
                    wordPattern = wordPattern.replace(Prolictic8, Prolictic88);
                    wordPattern = wordPattern.replace(Prolictic9, Prolictic99);
                    wordPattern = wordPattern.replace(Prolictic10, Prolictic1010);
                    //----------------------------------------------------------
                    wordPattern = wordPattern.replaceAll(" ", "");
                    wordPattern = wordPattern.replaceAll("_", " ");
                    //----------------------------------------------------------                    
                    return wordPattern;
                }
            }             
    }
        //----------------------------------------------------------------------
        char LastDiacritic = word.charAt(word.length() - 1);
        String Diacritic = "0_";
        String AlifLem = "0_";

        switch (LastDiacritic) {
            case 'َ':
                Diacritic = "1_";
                break;
            case 'ُ':
                Diacritic = "2_";
                break;
            case 'ِ':
                Diacritic = "3_";
                break;
            case 'ً':
                Diacritic = "4_";
                break;
            case 'ٌ':
                Diacritic = "5_";
                break;
            case 'ٍ':
                Diacritic = "6_";
                break;
            default:
                if (word.charAt(word.length() - 1) == 'ا') {
                    if (word.charAt(word.length() - 2) == 'َ') {
                        Diacritic = "1_";
                    }else if (word.charAt(word.length() - 2) == 'ً') {
                        Diacritic = "4_";
                    }else{
                        Diacritic = "0_";
                    }
                }
                
                break;
        }
        //----------------------------------------------------------------------
        String Ta3rif = word.substring(0, 2);        
        if(Ta3rif.equals("ال")){
            AlifLem = "1_";
        }
        
        //----------------------------------------------------------------------
        return "_مجهول_" + Diacritic + AlifLem;
    }
}