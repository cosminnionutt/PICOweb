package org.jugnicaragua.picoweb.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import org.jugnicaragua.picoweb.backend.modelo.Propietario;

public class PropietarioForm extends FormLayout {

    TextField nombre = new TextField("Nombre");
    TextField apellido = new TextField("Apellido");
    EmailField email = new EmailField("Email");

    Button save = new Button("Guardar");
    Button delete = new Button("Eliminar");
    Button close = new Button("Cancelar");

    Binder<Propietario> binder = new Binder<>(Propietario.class);
    private Propietario propietario;

    public PropietarioForm() {
        addClassName("propietario-form");
        binder.bindInstanceFields(this);
        add(nombre,
                apellido,
                email,
                createButtonsLayout());
    }

    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
        binder.readBean(propietario);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, propietario)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {

        try {
            binder.writeBean(propietario);
            fireEvent(new SaveEvent(this, propietario));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class PropietarioFormEvent extends ComponentEvent<PropietarioForm> {
        private Propietario propietario;

        protected PropietarioFormEvent(PropietarioForm source, Propietario propietario) {
            super(source, false);
            this.propietario = propietario;
        }

        public Propietario getPropietario() {
            return propietario;
        }
    }

    public static class SaveEvent extends PropietarioFormEvent {
        SaveEvent(PropietarioForm source, Propietario propietario) {
            super(source, propietario);
        }
    }

    public static class DeleteEvent extends PropietarioFormEvent {
        DeleteEvent(PropietarioForm source, Propietario propietario) {
            super(source, propietario);
        }
    }

    public static class CloseEvent extends PropietarioFormEvent {
        CloseEvent(PropietarioForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
