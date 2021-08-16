package pl.magneztech.views.entries;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;
import pl.magneztech.data.entity.Entry;
import pl.magneztech.data.service.EntryService;
import pl.magneztech.views.MainLayout;

import java.util.Optional;

@PageTitle("Entries")
@Route(value = "entries/:entryID?/:action?(edit)", layout = MainLayout.class)
public class EntriesView extends Div implements BeforeEnterObserver {

    private final String ENTRY_ID = "entryID";
    private final String ENTRY_EDIT_ROUTE_TEMPLATE = "entries/%d/edit";

    private final Grid<Entry> grid = new Grid<>(Entry.class, false);
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final BeanValidationBinder<Entry> binder;
    private final EntryService entryService;
    private TextField name;
    private NumberField kcal;
    private NumberField fat;
    private NumberField carbohydrate;
    private NumberField protein;
    private Entry entry;

    public EntriesView(@Autowired EntryService entryService) {
        addClassNames("entries-view", "flex", "flex-col", "h-full");
        this.entryService = entryService;
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("kcal").setAutoWidth(true).setHeader("kcal");
        grid.addColumn("fat").setAutoWidth(true);
        grid.addColumn("carbohydrate").setAutoWidth(true);
        grid.addColumn("protein").setAutoWidth(true);
        grid.addComponentColumn(r -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH));
            button.addClickListener(event -> {
                entryService.delete(r.getId());
                refreshGrid();
            });
            return button;
        }).setAutoWidth(true).setHeader("");
        grid.setDataProvider(new CrudServiceDataProvider<>(entryService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ENTRY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EntriesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Entry.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.forField(kcal).bind("kcal");
        binder.forField(fat).bind("fat");
        binder.forField(carbohydrate).bind("carbohydrate");
        binder.forField(protein).bind("protein");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.entry == null) {
                    this.entry = new Entry();
                }
                binder.writeBean(this.entry);

                entryService.update(this.entry);
                clearForm();
                refreshGrid();
                Notification.show("Entry details stored.");
                UI.getCurrent().navigate(EntriesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the entry details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> entryId = event.getRouteParameters().getInteger(ENTRY_ID);
        if (entryId.isPresent()) {
            Optional<Entry> entryFromBackend = entryService.get(entryId.get());
            if (entryFromBackend.isPresent()) {
                populateForm(entryFromBackend.get());
            } else {
                Notification.show(String.format("The requested entry was not found, ID = %d", entryId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(EntriesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        kcal = new NumberField("Kcal");
        fat = new NumberField("Fat");
        carbohydrate = new NumberField("Carbohydrate");
        protein = new NumberField("Protein");
        Component[] fields = new Component[]{name, kcal, fat, carbohydrate, protein};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Entry value) {
        this.entry = value;
        binder.readBean(this.entry);

    }
}
