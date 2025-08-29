package official.sketchBook.engine.util_related.utils;

import java.lang.reflect.Array;

public class ArrayUtils {
    @SuppressWarnings("unchecked")
    public static <T> T[] resizeArray(T[] original, int newSize) {
        // Cria um novo array do mesmo tipo do original
        T[] newArray = (T[]) Array.newInstance(
            original.getClass().getComponentType(), newSize
        );

        // Copia os elementos do original para o novo até o limite do menor tamanho
        System.arraycopy(original, 0, newArray, 0, Math.min(original.length, newSize));

        return newArray;
    }

    /**
     * Redimensiona uma array bidimensional (matriz).
     *
     * @param original matriz original
     * @param newRows  novo número de linhas
     * @param newCols  novo número de colunas (cada linha será redimensionada)
     * @return nova matriz redimensionada
     */
    @SuppressWarnings("unchecked")
    public static <T> T[][] resize2DArray(T[][] original, int newRows, int newCols) {
        // Redimensiona a primeira dimensão (linhas)
        T[][] newArray = resizeArray(original, newRows);

        // Redimensiona cada linha (colunas)
        for (int i = 0; i < newRows; i++) {
            if (i < original.length && original[i] != null) {
                newArray[i] = resizeArray(original[i], newCols);
            } else {
                // Cria nova linha vazia (importante para linhas além do tamanho antigo)
                newArray[i] = (T[]) Array.newInstance(
                        original.getClass().getComponentType().getComponentType(),
                        newCols
                );
            }
        }

        return newArray;
    }
}
