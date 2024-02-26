package org.jugnicaragua.picoweb.ui.view.list;

import org.jugnicaragua.picoweb.backend.modelo.Propietario;
import org.jugnicaragua.picoweb.backend.servicio.PropietarioServicio;
import org.jugnicaragua.picoweb.ui.PropietarioForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value="", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
@PageTitle("Clientes | Pico Web")
public class ListView extends VerticalLayout {

    private PropietarioServicio propietarioServicio;
    private Grid<Propietario> grid = new Grid<>(Propietario.class);
    private TextField filterText = new TextField();
    private PropietarioForm form;

    public ListView(PropietarioServicio propietarioServicio) {
        this.propietarioServicio = propietarioServicio;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new PropietarioForm();
        form.addListener(PropietarioForm.SaveEvent.class, this::savePropietario);
        form.addListener(PropietarioForm.DeleteEvent.class, this::deletePropietario);
        form.addListener(PropietarioForm.CloseEvent.class, e -> closeEditor());

        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();
        closeEditor();

    }

    private void savePropietario(PropietarioForm.SaveEvent event) {
        propietarioServicio.save(event.getPropietario());
        updateList();
        closeEditor();
    }

    private void deletePropietario(PropietarioForm.DeleteEvent event) {
        propietarioServicio.delete(event.getPropietario());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("propietario-grid");
        grid.setSizeFull();
        grid.setColumns("nombre", "apellido", "email");
        grid.asSingleSelect().addValueChangeListener(event ->
                editPropietario(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtar por Nombre...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        Button addPropietarioButton = new Button("Add propietario");
        addPropietarioButton.addClickListener(click -> addPropietario());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPropietarioButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        grid.setItems(propietarioServicio.findAll(filterText.getValue()));
    }

    public void editPropietario(Propietario propietario) {
        if (propietario == null) {
            closeEditor();
        } else {
            form.setPropietario(propietario);
            form.setVisible(true);
            addClassName("editing");
        }
    }
    private void closeEditor() {
        form.setPropietario(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    void addPropietario() {
        grid.asSingleSelect().clear();
        editPropietario(new Propietario());
    }

}
