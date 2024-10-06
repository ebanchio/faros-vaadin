package com.vaadin.faros;

import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.model.Employee;
import com.vaadin.utils.FarosUtils;

public class EmployeeDetail extends VerticalLayout {

    private Employee empleado;

    public EmployeeDetail() {
        addClassName("employeeDetail");
    }

    public void show(Employee e) {

        if (e == null) return;

        empleado = e;

        removeAll();

        HorizontalLayout detailTitleLayout = new HorizontalLayout();
        detailTitleLayout.addClassName("employeeDetailTitleLayout");

        Div detailTitle = new Div(e.getIdentity().getFullName());
        detailTitle.addClassName("employeeDetailTitle");

        FontAwesome.Solid.Icon openExternalButton = FontAwesome.Solid.ARROW_UP_RIGHT_FROM_SQUARE.create();
        openExternalButton.addClickListener(ev -> Notification.show("Abrir"));
        openExternalButton.addClassName("employeeDetailButton");

        FontAwesome.Solid.Icon closeButton = FontAwesome.Solid.XMARK.create();
        closeButton.addClickListener(ev -> removeCurrent());
        closeButton.addClassName("employeeDetailButton");

        detailTitleLayout.add(detailTitle, openExternalButton, closeButton);
        add(detailTitleLayout);

        addStatusLayout(e);
        addProfileInfo(e);
        addActionButtons();

        getStyle().set("display", "flex");
    }

    private void addStatusLayout(Employee e) {
        HorizontalLayout tag;

        Icon userIcon = FontAwesome.Solid.CIRCLE_USER.create();
        userIcon.addClassName("employeeTableStatusIcon");

        tag = new HorizontalLayout(userIcon, new Div(String.format(e.isActive() ? "Included - Active" : "Not included - Inactive")));
        tag.addClassName("employeeDetailStatusTag");
        tag.setSpacing(false);
        tag.setAlignItems(Alignment.CENTER);

        if (e.isActive()) {
            userIcon.addClassName("active");
            tag.addClassNames("active", "activeBackground");
        }
        else {
            userIcon.addClassName("inactive");
            tag.addClassNames("inactive", "inactiveBackground");
        }

        add(tag);
    }

    private void addActionButtons() {
        Button buttonSave = new Button("Save", event -> Notification.show("Save"));
        buttonSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button buttonCancel = new Button("Cancel", event -> removeCurrent());

        HorizontalLayout buttonsLayout = new HorizontalLayout(buttonSave, buttonCancel);
        buttonsLayout.addClassName("employeeDetailButtonsLayout");

        add(buttonsLayout);
    }

    private void addProfileInfo(Employee e) {
        VerticalLayout profileInfo = new VerticalLayout();
        profileInfo.setPadding(false);
        profileInfo.setSpacing(false);
        profileInfo.addClassName("employeeDetailProfileInfo");

        profileInfo.add(new Div("Profile info"));

        profileInfo.add(addTextField("UID", e.getUid(), ""));
        profileInfo.add(addButton("Title", "Add Title", FontAwesome.Solid.PLUS, ev -> Notification.show("Add title")));
        profileInfo.add(addTextField("Name", e.getIdentity().getFullName(), ""));
        profileInfo.add(addTextField("Email", e.getIdentity().getPrimaryEmail(), ""));
        profileInfo.add(addButton("Role", "Add Role", FontAwesome.Solid.PLUS, ev -> Notification.show("Add role")));
        profileInfo.add(addButton("Location", "Add Location", FontAwesome.Solid.PLUS, ev -> Notification.show("Add location")));
        profileInfo.add(addButton("Level", "Add Level", FontAwesome.Solid.PLUS, ev -> Notification.show("Add level")));

        add(profileInfo);
    }

    private VerticalLayout addButton(String label, String btnLabel, FontAwesome.Solid icon, ComponentEventListener<ClickEvent<Button>> r) {
        Button button = new Button();
        button.setPrefixComponent(icon.create());
        button.setText(btnLabel);
        button.addClickListener(r);

        VerticalLayout detailSectionLayout = new VerticalLayout(new Div(label), button);
        detailSectionLayout.setSpacing(false);
        detailSectionLayout.setPadding(false);

        detailSectionLayout.addClassName("employeeDetailFormField");

        return detailSectionLayout;
    }

    private TextField addTextField(String label, String value, String placeholder) {
        TextField tf = new TextField(label, value, placeholder);
        tf.addClassName("employeeDetailFormField");
        return tf;
    }

    private void removeCurrent() {
        empleado = null;
        getStyle().set("display", "none");
    }
}
