/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ipp.isep.dei.ESINF.dataStruc;

import pt.ipp.isep.dei.ESINF.domain.TrainStation;

import java.util.*;

/**
 *
 * @author DEI-ESINF
 * @param <E>
 */
public class AVL <E extends Comparable<E>> extends BST<E> {

    /**
     * Nested static class for an AVL tree node.
     * Extends BST.Node to add height information.
     */
    protected static class AVLNode<E extends Comparable<E>> extends Node<E> {
        private int height;
        private AVL<?> innerNode;  // lista de elementos com a mesma chave
        private int size;
        private  TrainStation trainStation;

        public AVLNode(E e, Node<E> left, Node<E> right) {
            super(e, left, right);
            this.innerNode = null;
            this.height = 1;
            this.size = 1;
        }

        public AVL getInnerNode() {
            return innerNode;
        }
        public void setInnerNode(AVL<E> innerNode) {
            this.innerNode = innerNode;
        }

        public TrainStation getTrainStation() {
            return trainStation;
        }

        public void setTrainStation(TrainStation trainStation) {
            this.trainStation = trainStation;
        }

        public void addElement(TrainStation station) {
            // Este método não é usado no modelo hierárquico, TrainStation é armazenado diretamente no nó folha.
            // Poderia ser usado se quisesse uma lista de estações em vez de um innerNode.
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getSize() {
            return size;
        }
        public void setSize(int size) {
            this.size = size;
        }

        @Override
        public AVLNode<E> getLeft() {
            return (AVLNode<E>) super.getLeft();
        }

        @Override
        public AVLNode<E> getRight() {
            return (AVLNode<E>) super.getRight();
        }


    }

    // ---------------------------------------------------------------------

    protected AVLNode<E> root = null;     // root of the tree
    Comparator<? super E> comparator;
    /* Constructs an empty binary search tree. */
    public AVL(Comparator<? super E> comparator) {
        root = null;
        this.comparator = comparator;
    }

    /*
     * @return root Node of the tree (or null if tree is empty)
     */
    public AVLNode<E> root() {
        return root;
    }

    /**
     * Retorna a altura de um nó. O(1)
     */
    protected int height(AVLNode<E> node) {
        if (node == null) return 0;
        return (node).getHeight();
    }

    /**
     * Retorna o tamanho (número de nós) da subárvore com raiz no nó dado. O(1)
     */
    protected int size(AVLNode<E> node) {
        if (node == null) return 0;
        return node.getSize();
    }

    /**
     * Retorna o fator de balanceamento de um nó. O(1)
     */
    private int balanceFactor(AVLNode<E> node){
        if (node == null) return 0;
        return height(node.getLeft()) - height(node.getRight());
    }

    /**
     * Atualiza a altura e o tamanho do nó.
     * O tamanho é 1 (o próprio nó) + size(left) + size(right).
     */
    private void updateHeightAndSize(AVLNode<E> node) {
        // 1. Atualiza Altura
        node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));

        // 2. Atualiza Tamanho (fundamental para findKElement)
        int leftSize = size(node.getLeft());
        int rightSize = size(node.getRight());

        node.setSize(1 + leftSize + rightSize);
    }

    /**
     * rightRotation:
     * - Executa uma rotação simples à direita para balancear a AVL.
     * - Complexidade temporal: O(1).
     *
     * */
    private AVLNode<E> rightRotation(AVLNode<E> node){
        AVLNode<E> leftChild = node.getLeft();
        AVLNode<E> temp = leftChild.getRight();

        //perform rotation
        leftChild.setRight(node);
        node.setLeft(temp);

        // update heights and sizes (bottom-up)
        updateHeightAndSize(node); // Node (antiga raiz) deve ser atualizado primeiro
        updateHeightAndSize(leftChild); // LeftChild (nova raiz) deve ser atualizado por último

        //return new root
        return leftChild;
    }

    /**
     * leftRotation:
     * * - Executa uma rotação simples à esquerda para balancear a AVL.
     * * - Complexidade temporal: O(1).
     * */
    private AVLNode<E> leftRotation(AVLNode<E> node){
        AVLNode<E> rightChild = node.getRight();
        AVLNode<E> temp = rightChild.getLeft();

        // efetua rotação
        rightChild.setLeft(node);
        node.setRight(temp);

        // atualiza alturas e tamanhos
        updateHeightAndSize(node); // Node (antiga raiz) deve ser atualizado primeiro
        updateHeightAndSize(rightChild); // RightChild (nova raiz) deve ser atualizado por último

        // retorna nova raiz
        return rightChild;
    }

    /**
     * * twoRotations:
     * * - Executa duas rotações (esquerda+direita ou direita+esquerda)
     * * - Complexidade temporal: O(1).
     * */
    private AVLNode<E> twoRotations(AVLNode<E> node){
        if (balanceFactor(node) > 1) { // desequilíbrio à esquerda (LR)
            node.setLeft(leftRotation(node.getLeft()));
            return rightRotation(node);
        } else if (balanceFactor(node) < -1) { // desequilíbrio à direita (RL)
            node.setRight(rightRotation(node.getRight()));
            return leftRotation(node);
        }
        return node;
    }

    /**
     * inOrder:
     * - Retorna todos os elementos da AVL em ordem crescente.
     * - Complexidade temporal: O(n).
     * */
    @Override
    public Iterable<E> inOrder() {
        List<E> result = new ArrayList<>();
        inOrderRec(this.root, result);
        return result;
    }

    private void inOrderRec(AVLNode<E> node, List<E> result) {
        if (node == null) return;
        inOrderRec(node.getLeft(), result);
        result.add(node.getElement());
        inOrderRec(node.getRight(), result);
    }

    private AVLNode<E> balanceNode(AVLNode<E> node) {
        int balance = balanceFactor(node);

        // caso esquerda-esquerda (LL)
        if (balance > 1 && balanceFactor(node.getLeft()) >= 0) {
            return rightRotation(node);
        }

        // caso direita-direita (RR)
        if (balance < -1 && balanceFactor(node.getRight()) <= 0) {
            return leftRotation(node);
        }

        // caso esquerda-direita (LR)
        if (balance > 1 && balanceFactor(node.getLeft()) < 0) {
            node.setLeft(leftRotation(node.getLeft()));
            return rightRotation(node);
        }

        // caso direita-esquerda (RL)
        if (balance < -1 && balanceFactor(node.getRight()) > 0) {
            node.setRight(rightRotation(node.getRight()));
            return leftRotation(node);
        }

        // já equilibrado
        return node;
    }

    /**
     * * Insere uma estação na AVL seguindo a hierarquia de chaves.
     * * Complexidade temporal: O(h * log n).
     */
    public void insertAVL(Object[] hierarchy, TrainStation station) {
        root = insertRec(root, hierarchy, 0, station);
    }

    private AVLNode<E> insertRec(AVLNode<E> node, Object[] hierarchy, int depth, TrainStation station) {
        E level =  (E) hierarchy[depth];

        if (node == null) node = new AVLNode<>(level, null, null);

        int cmp = comparator.compare(level, node.getElement());
        if (cmp < 0) {
            node.setLeft(insertRec(node.getLeft(), hierarchy, depth, station));
        } else if (cmp > 0) {
            node.setRight(insertRec(node.getRight(), hierarchy, depth, station));
        } else {
            // key matches -> go to next level of hierarchy if not last
            if (depth < hierarchy.length - 1) {
                if (node.getInnerNode() == null) {
                    // Usamos o mesmo comparador para a sub-árvore
                    node.setInnerNode(new AVL<E>(comparator));
                }
                // Chamada recursiva na sub-árvore, avançando a profundidade
                node.getInnerNode().insertHelper(hierarchy, station, depth + 1);
            } else {
                // last level
                node.setTrainStation(station);
                // Não precisa de updateSize aqui, pois o size já foi definido em AVLNode
                // e o size de nós internos só muda com a adição de filhos (já tratado abaixo).
            }
        }

        // update height and size, then rebalance
        updateHeightAndSize(node);
        return balanceNode(node);
    }

    private void insertHelper(Object[] hierarchy, TrainStation station, int depth) {
        root = insertRec(root, hierarchy, depth, station);
    }


    public void remove(E element, TrainStation station){
        root = remove(element, root(), station);
    }

    private AVLNode<E> remove(E element, AVLNode<E> node, TrainStation station) {
        if (node == null) {
            return null;
        }

        int cmp = element.compareTo(node.getElement());
        if (cmp == 0) {
            AVLNode<E> avlNode = node;

            // Tratamento de InnerNode (simplificado, mas que deve ser ajustado para a lógica real de remoção hierárquica)
            // Se houvesse uma innerNode, a remoção deveria ser feita primeiro nela.

            // caso normal (só um elemento na lista)
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            }
            if (node.getLeft() == null) {
                return node.getRight();
            }
            if (node.getRight() == null) {
                return node.getLeft();
            }

            // tem dois filhos - substitui pelo menor da subárvore direita
            E smallElem = smallestElement(node.getRight());
            node.setElement(smallElem);
            node.setRight(remove(smallElem, node.getRight(), station));
        }
        else if (cmp < 0) {
            node.setLeft(remove(element, node.getLeft(), station));
        }
        else {
            node.setRight(remove(element, node.getRight(), station));
        }

        // Atualiza altura e tamanho
        updateHeightAndSize(node);
        return balanceNode(node);
    }

    public boolean equals(Object otherObj) {

        if (this == otherObj)
            return true;

        if (otherObj == null || this.getClass() != otherObj.getClass())
            return false;

        AVL<E> second = (AVL<E>) otherObj;
        return equals(root, second.root);
    }

    public boolean equals(Node<E> root1, Node<E> root2) {
        if (root1 == null && root2 == null)
            return true;
        else if (root1 != null && root2 != null) {
            if (root1.getElement().compareTo(root2.getElement()) == 0) {
                return equals(root1.getLeft(), root2.getLeft())
                        && equals(root1.getRight(), root2.getRight());
            } else
                return false;
        }
        else return false;
    }

    /**
     * Verifica se a AVL contém um nó com a chave fornecida. O(log n).
     */
    public boolean contains(E key) {
        return findNode(key, root) != null;
    }


    /**
     * Busca o nó que corresponde à chave fornecida. O(log n).
     */
    public AVLNode<E> findNode(E element, AVLNode<E> node) {
        if (node == null) return null;

        int cmp = element.compareTo(node.getElement());
        if (cmp == 0) return node;
        if (cmp < 0) return findNode(element, node.getLeft());
        else return findNode(element, node.getRight());
    }

    /**
     * searchExact:
     * - Busca exata: O(log n + m).
     */
    public List<TrainStation> searchExact(E key) {
        AVLNode<E> node = findNode(key, root);
        if (node == null) return List.of();

        List<TrainStation> list = new ArrayList<>();

        // Se houver estação no nó (last level)
        if (node.getTrainStation() != null) {
            list.add(node.getTrainStation());
        }

        // Se houver innerNode → recolhe todas as estações recursivamente
        if (node.getInnerNode() != null) {
            collectStations((AVLNode<?>) node.getInnerNode().root, list);
        }

        return list;
    }

    /** collectStations:
     * - Percorre todos os nós da sub-AVL recursivamente.
     * - Complexidade linear O(m), visitando cada estação exatamente uma vez.
     */
    private void collectStations(AVLNode<?> node, List<TrainStation> out) {
        if (node == null) return;

        collectStations(node.getLeft(), out);

        if (node.getTrainStation() != null)
            out.add(node.getTrainStation());

        if (node.getInnerNode() != null)
            collectStations((AVLNode<?>) node.getInnerNode().root, out);

        collectStations(node.getRight(), out);
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        toStringRec(root, 0, sb);
        return sb.toString();
    }

    private void toStringRec(Node<E> root, int level, StringBuilder sb){
        if(root==null)
            return;
        toStringRec(root.getRight(), level+1, sb);
        if (level!=0){
            for(int i=0;i<level-1;i++)
                sb.append("|\t");
            sb.append("|-------"+root.getElement()+"\n");
        }
        else
            sb.append(root.getElement()+"\n");
        toStringRec(root.getLeft(), level+1, sb);
    }


    /**
     * printTree:
     * - Percorre recursivamente a árvore AVL imprimindo cada nó e, se existir, seu innerNode. O(n_total).
     */
    // Print a node and its hierarchy recursively
    public static void printTree(AVLNode<?> node, int level) {
        if (node == null) return;

        // First traverse left
        printTree(node.getLeft(), level);

        // Print current node with indentation
        String indent = "  ".repeat(level);
        if (node.getTrainStation() != null) {
            System.out.println(indent + node.getElement() + " -> " + node.getTrainStation());
        } else {
            System.out.println(indent + node.getElement());
        }

        // Traverse next level
        if (node.getInnerNode() != null) {
            printTree((AVLNode<?>) node.getInnerNode().root, level + 1);
        }
        // Then traverse right
        printTree(node.getRight(), level);
    }

    // Convenience method for the whole tree
    public static void printTree(AVL<?> tree) {
        printTree(tree.root, 0);
    }

    /**
     * Retorna o k-ésimo menor elemento (com k começando em 0).
     * Usa o campo `size` para decidir a direção de busca. O(log n).
     */
    public E findKElement(int k) {
        if (root == null || k < 0 || k >= size(root)) {
            return null;
        }
        return findElementRec(root, k);
    }

    private E findElementRec(AVLNode<E> node, int k) {
        if (node == null) {
            return null;
        }

        int L = size(node.getLeft());

        if (k == L) {
            return node.getElement();
        } else if (k < L) {
            return findElementRec(node.getLeft(), k);
        } else {
            return findElementRec(node.getRight(), k - (L + 1));
        }
    }

    /**
     * @return Retorna o total de nós na AVL principal. O(1).
     */
    @Override
    public int size() {
        return size(root);
    }
}