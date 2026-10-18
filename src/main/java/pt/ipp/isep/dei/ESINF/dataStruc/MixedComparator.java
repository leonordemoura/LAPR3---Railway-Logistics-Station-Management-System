package pt.ipp.isep.dei.ESINF.dataStruc;

import java.util.Comparator;
/**
 * Comparador genérico para hierarquias mistas de chaves em AVLs.
 *
 * <p>
 * Este comparador permite comparar objetos que podem ser:
 * <ul>
 *     <li>Double vs Double: compara numericamente</li>
 *     <li>String vs String: compara lexicograficamente</li>
 *     <li>Double vs String ou String vs Double: considera Double < String</li>
 * </ul>
 * </p>
 *
 * <p>
 * É utilizado em AVLs que armazenam chaves heterogêneas (como latitude/nome ou timezone/country/nome).
 * O uso de Object permite flexibilidade em hierarquias genéricas.
 * </p>
 *
 * <p>
 * Complexidade:
 * - Comparar dois elementos com este comparador é O(1), pois cada verificação de tipo e comparação
 *   de valores é feita em tempo constante.
 * </p>
 */
public class MixedComparator {

    public static final Comparator<Object> MIXED_COMPARATOR = new Comparator<>() {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null || o2 == null)
                throw new IllegalArgumentException("Keys cannot be null");

            if (o1 instanceof Double && o2 instanceof Double)
                return Double.compare((Double)o1, (Double)o2);

            if (o1 instanceof String && o2 instanceof String)
                return  ((String) o1).compareTo((String)o2);

            // Mixed types: Double < String
            if (o1 instanceof Double && o2 instanceof String) return -1;
            if (o1 instanceof String && o2 instanceof Double) return 1;

            throw new IllegalArgumentException("Unsupported key types: " + o1.getClass() + " and " + o2.getClass());
        }
    };
}
