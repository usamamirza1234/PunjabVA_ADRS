package ast.adrs.va.HomeAuxiliares.adapters;


public interface SectionStateChangeListener {
    void onSectionStateChanged(Section section, boolean isOpen);
}