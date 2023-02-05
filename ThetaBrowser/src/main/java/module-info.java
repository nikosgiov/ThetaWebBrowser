module com.nikosgiov.thetabrowser {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.nikosgiov.thetabrowser to javafx.fxml;
    exports com.nikosgiov.thetabrowser;
}