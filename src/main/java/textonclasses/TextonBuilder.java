package textonclasses;

import java.awt.image.BufferedImage;
import java.util.List;

public class TextonBuilder {
    BufferedImage bImage;
    private Integer numTexton;
    private String source;
    private String name;
    private TextonType type;
    private List<LienTexton> lienTexton;
    private boolean isCompleted = false;
    private String description;
    private int timer;

    public TextonBuilder setLienTexton(List<LienTexton> lienTexton) {
        this.lienTexton = lienTexton;
        return this;
    }

    public TextonBuilder setTimer(int timer) {
        this.timer = timer;
        return this;
    }

    public TextonBuilder setbImage(BufferedImage bImage) {
        this.bImage = bImage;
        return this;
    }

    public TextonBuilder setNumTexton(Integer numTexton) {
        this.numTexton = numTexton;
        return this;
    }

    public TextonBuilder setSource(String source) {
        this.source = source;
        return this;
    }

    public TextonBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TextonBuilder setType(TextonType type) {
        this.type = type;
        return this;
    }

    public TextonBuilder setCompleted(boolean completed) {
        isCompleted = completed;
        return this;
    }

    public TextonBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public Texton createTexton() {
        Texton texton = null;
        switch (type) {
            case TT:
                texton = new TextonT(numTexton, source, name, bImage);
                break;
            case TM:
                texton = new TextonM(numTexton, source, name);
                break;
            case TV:
                texton = new TextonV(numTexton, source, name, timer, bImage);
        }
        texton.setDescription(description);
        texton.getLienTexton().addAll(lienTexton);
        validate(texton);
        return texton;
    }

    private void validate(Texton texton) {
        if (texton == null) throwException("Could not instanciate texton, null during validation.");
        if (bImage == null && texton.getType() != TextonType.TM) throwException("Buffered Image must not be null.");
        if (texton.getNumTexton() < 1 || texton.getNumTexton() > 1000)
            throwException("Texton Number is not valid, must be between 1 and 1000 inclusively.");
        if (texton.getType() == null) throwException("Invalid texton type.");
        if (texton.getDescription() == null || texton.getDescription().isEmpty())
            throwException("Texton description is not initialized or is empty.");
        if (texton.getLienTexton().stream().anyMatch(lienTexton1 -> !lienTexton1.isValid()) || texton.getLienTexton().size() > 4)
            throwException("Texton links are not valid.");
        if (texton instanceof TextonV && ((TextonV) texton).getTimer() < TextonV.MINIMUMTIMER)
            throwException("TextonV Timer must be > " + TextonV.MINIMUMTIMER);
    }

    private void throwException(String str) {
        throw new IllegalStateException(str);
    }
}