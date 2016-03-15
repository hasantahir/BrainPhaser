package de.fhdw.ergoholics.brainphaser.fileimport;

import org.w3c.dom.Node;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.fhdw.ergoholics.brainphaser.BrainPhaserApplication;
import de.fhdw.ergoholics.brainphaser.activities.BrainPhaserActivity;
import de.fhdw.ergoholics.brainphaser.fileimport.bpc.BPCObjects;
import de.fhdw.ergoholics.brainphaser.fileimport.bpc.BPCRead;
import de.fhdw.ergoholics.brainphaser.fileimport.bpc.BPCWrite;
import de.fhdw.ergoholics.brainphaser.fileimport.exceptions.FileFormatException;
import de.fhdw.ergoholics.brainphaser.fileimport.exceptions.UnexpectedElementException;
import de.fhdw.ergoholics.brainphaser.model.Answer;
import de.fhdw.ergoholics.brainphaser.model.Category;
import de.fhdw.ergoholics.brainphaser.model.Challenge;

/**
 * Created by Daniel Hoogen on 19/02/2016.
 *
 * Contains the logic for importing files
 */
public class FileImport {
    /**
     * Imports a .bpc file containing categories with challenges and their answers into the database
     * @param is the input stream of the file to be imported
     * @param application the BrainPhaserApplication instance
     * @throws FileFormatException if file is no xml file
     * @throws UnexpectedElementException if an unexpected element was fond in the file
     */
    public static void importBPC(InputStream is, BrainPhaserApplication application)
            throws FileFormatException, UnexpectedElementException {

        //Get root element
        Node categoriesNode = BPCRead.getCategoriesNode(is);

        //Get the root's child nodes
        Node childCategories = categoriesNode.getFirstChild();

        //Create lists for saving the categories, challenges and answers
        List<Category> categoryList = new ArrayList<>();
        List<Challenge> challengeList = new ArrayList<>();
        List<Answer> answerList = new ArrayList<>();

        //All categories, challenges and answers are read first
        //So if there is any syntax error in the file, nothing will be imported
        long i = 0;
        long nextChallengeId = 0;
        while (childCategories!=null) {
            if (childCategories.getNodeType()==Node.ELEMENT_NODE) {
                nextChallengeId = BPCObjects.readCategory(childCategories, i,
                        nextChallengeId, categoryList, challengeList, answerList);
                i++;
            }

            childCategories = childCategories.getNextSibling();
        }

        //No syntax errors were found, so the read information is being written
        BPCWrite writer = new BPCWrite(application);
        writer.writeAll(categoryList, challengeList, answerList);
    }
}