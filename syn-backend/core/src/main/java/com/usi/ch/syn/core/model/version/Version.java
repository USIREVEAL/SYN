package com.usi.ch.syn.core.model.version;

import com.usi.ch.syn.core.model.CodeEntity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Version<V extends Version<?>> extends CodeEntity {

    private V next;
    private V previous;

    protected Version(int id) {
        super(id);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Version)) return false;
//        if (!super.equals(o)) return false;
//        Version<?> version = (Version<?>) o;
//        return Objects.equals(getNext(), version.getNext()) && Objects.equals(getPrevious(), version.getPrevious());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(super.hashCode(), getNext(), getPrevious());
//    }
}
