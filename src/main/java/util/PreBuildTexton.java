package util;

import textonclasses.LienTexton;
import textonclasses.TextonType;
import textonclasses.TextonV;

import java.awt.image.BufferedImage;
import java.util.List;

public class PreBuildTexton {
    int num;
    String name;
    String source;
    TextonType type;
    int timer;
    String desc;
    List<LienTexton> liens;
    BufferedImage bImage;

    private PreBuildTexton(int num, String name, String source, TextonType type, int timer, String desc, List<LienTexton> liens) {
        //Integrity checks that all throw IllegalStateExceptions for build() method of builder to catch.
        if (num <= 0) throw new IllegalStateException("PreBuildTexton num must be > 0.");
        this.num = num;
        if(name.isEmpty()) throw new IllegalStateException("PreBuildTexton name must not be empty.");
        this.name = name;
        //No validity check for source
        this.source = source;
        if(type == null) throw new IllegalStateException("PreBuildTexton type must not be null.");
        this.type = type;
        if(type.equals(TextonType.TM) && timer < TextonV.MINIMUMTIMER)
            throw new IllegalStateException("PrebuildTexton timer must be > " + TextonV.MINIMUMTIMER + " if type is TM.");
        this.timer = timer;
        //No validity check for description
        this.desc = desc;
        if(!liens.stream().allMatch(LienTexton::isValid))
            throw new IllegalStateException("PrebuildTexton links are invalid. Dumping links..." + liens.toString());
        this.liens = liens;
        if(bImage == null && (type.equals(TextonType.TT) || type.equals(TextonType.TV)))
            throw new IllegalStateException("PrebuildTexton Buffered Image must not be null if type is TT or TV.");
        if (type == TextonType.TM) bImage = null;
    }

    public static class PreBuildTextonBuilder {
        private int num;
        private String name;
        private String source;
        private TextonType type;
        private int timer = 0;
        private String desc;
        private List<LienTexton> liens;
        BufferedImage bImage;

        public PreBuildTextonBuilder setNum(int num) {
            this.num = num;
            return this;
        }

        public PreBuildTextonBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public PreBuildTextonBuilder setSource(String source) {
            this.source = source;
            return this;
        }

        public PreBuildTextonBuilder setType(TextonType type) {
            this.type = type;
            return this;
        }

        public PreBuildTextonBuilder setTimer(int timer) {
            this.timer = timer;
            return this;
        }

        public PreBuildTextonBuilder setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public PreBuildTextonBuilder setLiens(List<LienTexton> liens) {
            this.liens = new MaxedArrayList<>(4);
            this.liens.addAll(liens);
            this.liens = liens;
            return this;
        }

        public PreBuildTextonBuilder setBimage(BufferedImage bImage) {
            this.bImage = bImage;
            return this;
        }

        public PreBuildTexton build() {
            try {
                return new PreBuildTexton(num, name, source, type, timer, desc, liens);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}