package io;

import textonclasses.Texton;
import util.Util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-09-28.
 */
public class XmlFileConnector extends TextonIo {

    private Path path;

    @SuppressWarnings("unchecked")
    public XmlFileConnector(String[] properties) throws IOException{
        super(properties);
        Path masterFile = Paths.get(properties[0]);
        masterFile.normalize();
        Properties masterProperties = new Properties();
        try {
            masterProperties.load(Files.newInputStream(masterFile));
            path = masterFile.getParent();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Texton readTexton(int numTexton) throws IOException {
        String formattedNum = Util.getFormattedNumSerie(numTexton);
        Path textonPath = path.resolve(formattedNum + ".json");
        Path imagePath = null;

        //Vérifier tous les types d'image supportés par Java et créer un Path par image trouvée.
        List<Path> collect = Stream.of("jpg", "jpeg", "bmp", "wbmp", "png", "gif")
                .filter(s -> Files.exists(Paths.get(path.toString() + formattedNum + "." + s))).limit(1)
                .map(s -> Paths.get(path.toString() + formattedNum + "." + s)).collect(Collectors.toList());

        if(collect.size() > 1){
            System.out.println("Attention: plus d'une image a été trouvée pour le texton " + numTexton);
        } else if (collect.size() == 1) {
            imagePath = collect.get(0);
        }

        //TODO Compléter cette méthode et retourner une valeur.
        return null;
    }

    @Override
    public void writeTexton(Texton texton, boolean overwrite) throws IOException {
        try {
            try (FileOutputStream fout = new FileOutputStream(Paths.get(path.toString(), texton.getNumTexton() + ".xml").toFile())) {

                JAXBContext jc = JAXBContext.newInstance(Texton.class);

                Marshaller marshaller = jc.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(texton, fout);
            }
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String getTextonName(int numTexton) throws IOException {
        //TODO Écrire cette méthode
        return null;
    }

    @Override
    public String getGraphXml() throws IOException {
        //TODO Écrire cette méthode
        return "";
    }

    public static void main(String[] args) {

    }
}
