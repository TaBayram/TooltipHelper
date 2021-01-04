package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

public class Controller {


    public TextField textField_Level1;
    public ColorPicker colorPicker_Color;
    public TextField textField_Levels;
    public TextArea textArea_Result;
    public TextFlow textFlow_Colored;
    public ColorPicker colorPicker_ColorSelected;

    IndexRange selection;
    String beforeText;
    IndexRange beforeSelection;

    public void initialize() throws BackingStoreException {

        colorPicker_Color.setValue(Color.valueOf("#ffcc00"));
        textArea_Result.setPromptText("|cffffcc00Level 1|r - Deals <A021,DataA1> |cffff0000damage|r! \n" +
                "|cffffcc00Level 2|r - Deals <A021,DataA2> |cffff0000damage|r! \n" +
                "|cffffcc00Level 3|r - Deals <A021,DataA3> |cffff0000damage|r! \n");




    }





    public void Generate(ActionEvent actionEvent) {

        String color = colorPicker_Color.getValue().toString();
        var textColor = ColorFormat(color);


        String level1 = textField_Level1.getText();

        String[] splitted = level1.split("1>");

        String levelsText = textField_Levels.getText();
        int levels = 0;
        try{
            levels = Integer.parseInt(levelsText);

        }catch(Exception e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Level amount can't be non numeric.");
            alert.showAndWait();
            return;

        }


        List<String> generatedLevels = new ArrayList<>();
        for(int i = 1; i <= levels; i ++){
            String textIndicator = textColor + "Level " + i + "|r - ";
            String level = "";
            for (int j = 0; j < splitted.length; j++){
                level += splitted[j];
                if(j != splitted.length-1) level+= i+">";
            }
            generatedLevels.add(textIndicator+level);


        }

        textArea_Result.setText("");
        for (String string:generatedLevels) {
            textArea_Result.setText(textArea_Result.getText() + string + " \n");

        }
        GenerateColoredText(null);


    }


    public void GenerateColoredText(KeyEvent keyEvent) {
        String[] splittedText = textArea_Result.getText().replace("|c","cc").split("ccff");


        textFlow_Colored.getChildren().clear();
        int newHeight = 100;
        for(int i = 0; i < splittedText.length; i ++){
            try {
                String[] endSplitted = splittedText[i].replace("|r","rrrrrrrr").split("rrrrrrrr");

                Text text = new Text();
                text.setText(endSplitted[0].substring(6));
                text.setFill(Color.valueOf(splittedText[i].substring(0, 6)));

                if(endSplitted.length == 1){
                    textFlow_Colored.getChildren().add(text);
                }
                else{

                    String nonColored = "";
                    for(int j = 1; j < endSplitted.length; j++){
                        nonColored += endSplitted[j];

                    }

                    Text text1 = new Text();
                    text1.setText(nonColored);




                    textFlow_Colored.getChildren().addAll(text,text1);

                    System.out.println("1:" +text.getBoundsInLocal().getHeight());
                    System.out.println("2:" + (text1.getBoundsInLocal().getHeight() - text1.getFont().getSize()*1.4));
                    if(text1.getBoundsInLocal().getHeight() > text.getBoundsInLocal().getHeight())
                        newHeight += text1.getBoundsInLocal().getHeight() - text1.getFont().getSize()*1.4;
                    else
                        newHeight += text.getBoundsInLocal().getHeight();

                }


            }
            catch(StringIndexOutOfBoundsException stringIndexOutOfBoundsException){
                continue;
            }
        }

        textFlow_Colored.setMinHeight(newHeight);

    }

    public void Colorify(ActionEvent actionEvent) {

        if(selection == null || selection.getLength() == 0 ){
            return;
        }

        else{
            var color = ColorFormat(colorPicker_ColorSelected.getValue().toString());
            var fullText = textField_Level1.getText();

            if(beforeSelection == selection){
                fullText = beforeText;
            }


            beforeSelection = selection;
            beforeText = fullText +"";
            String first = fullText.substring(0,selection.getStart()) + color+ fullText.substring(selection.getStart(), selection.getEnd()) + "|r" + fullText.substring(selection.getEnd());
            textField_Level1.setText(first);


        }

    }

    public void SetSelectedText(MouseEvent mouseEvent) {
        selection = textField_Level1.getSelection();
    }


    public String ColorFormat(String rawColor){
        rawColor = rawColor.replaceFirst("0x","");
        rawColor = rawColor.substring(0,6);

        String textColor = "|cff"+rawColor;
        return  textColor;
    }


}
