package io;

import javafx.embed.swing.SwingFXUtils;
import textonclasses.Graph;
import textonclasses.Texton;
import textonclasses.TextonHeader;
import util.Util;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-09-28.
 */
public class XmlFileConnector extends TextonFileIo {

    public XmlFileConnector(Path path) throws IOException, URISyntaxException {
        super(path);
    }

    @Override
    public Texton readTexton(int numTexton) throws IOException {
        String formattedNum = Util.getFormattedNumSerie(numTexton);
        Path textonPath = getPath().resolve(formattedNum + ".xml");
        Path imagePath = null;

        //Vérifier tous les types d'image supportés par Java et créer un Path par image trouvée.
        List<Path> collect = Stream.of("jpg", "jpeg", "bmp", "wbmp", "png", "gif")
                .filter(s -> Files.exists(Paths.get(getPath().toString() + formattedNum + "." + s))).limit(1)
                .map(s -> Paths.get(getPath().toString() + formattedNum + "." + s)).collect(Collectors.toList());

        if (collect.size() > 1) {
            System.out.println("Attention: plus d'une image a été trouvée pour le texton " + numTexton);
        } else if (collect.size() == 1) {
            imagePath = collect.get(0);
        }

        try (FileReader reader = new FileReader(textonPath.toString())) {
            JAXBContext jaxbContext = JAXBContext.newInstance(Texton.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Texton texton = (Texton) unmarshaller.unmarshal(reader);
            unmarshaller = jaxbContext.createUnmarshaller();
            return texton;
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeTexton(Texton texton, boolean overwrite) throws IOException {
        try {
            try (FileOutputStream fout = new FileOutputStream(Paths.get(getPath().toString(),
                    Util.getFormattedNumSerie(texton.getNumTexton()) + ".xml").toFile())) {

                JAXBContext jc = JAXBContext.newInstance(Texton.class);
                Marshaller marshaller = jc.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(texton, fout);

                //Save image
                File file = new File(getPath().toString(), Util.getFormattedNumSerie(texton.getNumTexton()) + ".png");
                String format = "png";
                ImageIO.write(SwingFXUtils.fromFXImage(texton.getImage(), null), format, file);
            }
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Graph getGraph() throws IOException {
        return GraphConnector.getGraph(getPath());
    }

    @Override
    public TextonHeader readTextonHeader(int numTexton) throws IOException {
        return null;
    }

}
