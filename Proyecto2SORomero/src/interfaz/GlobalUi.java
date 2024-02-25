/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaz;


public class GlobalUi {
    private static final MainPage mainPage = new MainPage();
    
    public static MainPage getMainPage() {
        return mainPage;
    }

    public static void openMainPage() {
        getMainPage().setVisible(true);  
    }
}
