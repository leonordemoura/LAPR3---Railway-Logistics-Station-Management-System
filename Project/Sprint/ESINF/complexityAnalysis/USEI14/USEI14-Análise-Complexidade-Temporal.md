# USEI14 - Análise de Complexidade Temporal

## Contexto
A eficácia dos principais hubs depende diretamente dos corredores que os ligam. Devido à capacidade limitada dos trilhos, é essencial que os planeadores compreendam o maximum throughput possível entre hubs. Esta compreensão é fundamental para validar horários, identificar bottlenecks na rede e definir prioridades para upgrades e melhorias da infraestrutura. Ao calcular o maximum flow, é possível estabelecer um limite superior credível para o número de comboios que podem ser encaminhados por dia entre dois hubs.

### User Story
> **Como analista de operações**, quero calcular o **maximum flow** entre duas **estações selecionadas** (**source** e **sink**), considerando as **capacidades de cada ligação** (*per-edge capacities*), de forma a estimar o **theoretical throughput** do **corredor**.

---

## Análise da Complexidade Temporal

### Variáveis de Análise

- **n**: Número de vértices (estações)
- **m**: Número de arestas (linhas/conexões)
- **c**: Capacidade máxima por aresta
- **f**: Valor do flow máximo total

---

## Método Principal

### computeMaxFlow(V source, V sink)

| Linha | Código                                                                                | Complexidade Temporal | Justificação                                              |
|-------|--------------------------------------------------------------------------------------|-----------------------|-----------------------------------------------------------|
| 49-50 | `if (!originalGraph.validVertex(source) \|\| !originalGraph.validVertex(sink))`     | **O(1)**              | Validação de vértices (assumindo validVertex é O(1))     |
| 52-53 | `if (source.equals(sink))`                                                           | **O(1)**              | Comparação de vértices                                    |
| 55    | `initializeResidualCapacity();`                                                      | **O(n+m)**            | Inicialização do grafo residual                           |
| 56    | `maxFlowValue = 0;`                                                                  | **O(1)**              | Atribuição de variável                                    |
| 57    | `augmentingPaths.clear();`                                                           | **O(1)**              | Limpeza de lista                                          |
| 59    | `int iteration = 0;`                                                                 | **O(1)**              | Inicialização de contador                                 |
| 61    | `while (true)`                                                                       | **O(n·m)**            | Itera até não haver mais caminhos aumentantes             |
| 62    | `iteration++;`                                                                       | **O(1)**              | Incremento de variável                                    |
| 63    | `List<V> path = findAugmentingPathBFS(source, sink);`                               | **O(n+m)**            | Busca BFS no grafo residual                               |
| 64    | `if (path == null \|\| path.isEmpty()) break;`                                      | **O(1)**              | Verificação de condição de paragem                        |
| 66    | `int bottleneck = findMinCapacity(path);`                                            | **O(n)**              | Percorre o caminho (até n-1 arestas)                      |
| 67    | `updateResidualCapacity(path, bottleneck);`                                          | **O(n)**              | Atualiza capacidades ao longo do caminho                  |
| 69    | `maxFlowValue += bottleneck;`                                                        | **O(1)**              | Soma ao fluxo total                                       |
| 70    | `augmentingPaths.add(new FlowPath<>(...));`                                          | **O(n)**              | Criação do objeto FlowPath (copia a lista)                |
| 73    | `return new MaxFlowResult<>(...);`                                                   | **O(f)**              | Criação do resultado (copia lista de f caminhos)          |

### Complexidade Temporal Total:
**O(n+m) + O(n·m·(n+m+n+n)) = O(n·m²)**

---

## Métodos Auxiliares

### initializeResidualCapacity()

| Linha | Código                                                | Complexidade Temporal | Justificação                       |
|-------|-------------------------------------------------------|-----------------------|------------------------------------|
| 27    | `residualCapacity = new HashMap<>();`                 | **O(1)**              | Criação do mapa                    |
| 29    | `for (V u : originalGraph.vertices())`                | **O(n)**              | Itera sobre todos os vértices      |
| 30    | `residualCapacity.putIfAbsent(u, new HashMap<>());`   | **O(1)**              | Inserção em HashMap                |
| 32    | `for (Edge<V, Integer> edge : ...)`                   | **O(m)**              | Itera sobre todas as arestas       |
| 33    | `V v = edge.getVDest();`                              | **O(1)**              | Acesso a atributo                  |
| 34    | `int capacity = edge.getWeight();`                    | **O(1)**              | Acesso a atributo                  |
| 36    | `if (capacity <= 0) continue;`                        | **O(1)**              | Verificação condicional            |
| 39    | `residualCapacity.get(u).put(v, capacity);`           | **O(1)**              | Inserção no mapa (aresta direta)   |
| 42    | `residualCapacity.putIfAbsent(v, new HashMap<>());`   | **O(1)**              | Inserção no mapa                   |
| 43    | `residualCapacity.get(v).putIfAbsent(u, 0);`          | **O(1)**              | Inserção da aresta reversa         |

### Complexidade Temporal Total:
**O(n+m)**

---

### findAugmentingPathBFS(V source, V sink)

| Linha | Código                                                    | Complexidade Temporal | Justificação                                   |
|-------|-----------------------------------------------------------|-----------------------|------------------------------------------------|
| 77    | `Set<V> visited = new HashSet<>();`                       | **O(1)**              | Criação do conjunto                            |
| 78    | `Map<V, V> parent = new HashMap<>();`                     | **O(1)**              | Criação do mapa                                |
| 79    | `Queue<V> queue = new LinkedList<>();`                    | **O(1)**              | Criação da fila                                |
| 81    | `queue.add(source);`                                      | **O(1)**              | Inserção na fila                               |
| 82    | `visited.add(source);`                                    | **O(1)**              | Inserção no conjunto                           |
| 84    | `while (!queue.isEmpty())`                                | **O(n)**              | Cada vértice é visitado no máximo uma vez      |
| 85    | `V u = queue.poll();`                                     | **O(1)**              | Remoção da fila                                |
| 86    | `Map<V, Integer> neighbors = residualCapacity.get(u);`    | **O(1)**              | Acesso ao mapa                                 |
| 87    | `if (neighbors == null) continue;`                        | **O(1)**              | Verificação condicional                        |
| 89    | `for (Map.Entry<V, Integer> entry : ...)`                 | **O(m)**              | Cada aresta é considerada no máximo uma vez    |
| 90    | `V v = entry.getKey();`                                   | **O(1)**              | Acesso à chave                                 |
| 91    | `int capacity = entry.getValue();`                        | **O(1)**              | Acesso ao valor                                |
| 93    | `if (capacity > 0 && !visited.contains(v))`               | **O(1)**              | Verificação no HashSet                         |
| 94    | `visited.add(v);`                                         | **O(1)**              | Inserção no conjunto                           |
| 95    | `parent.put(v, u);`                                       | **O(1)**              | Inserção no mapa                               |
| 96    | `if (v.equals(sink))`                                     | **O(1)**              | Comparação                                     |
| 97    | `return reconstructPath(parent, source, sink);`           | **O(n)**              | Reconstrução do caminho                        |
| 99    | `queue.add(v);`                                           | **O(1)**              | Inserção na fila                               |
| 103   | `return null;`                                            | **O(1)**              | Retorno                                        |

### Complexidade Temporal Total:
**O(n+m)**

---

### reconstructPath(Map<V, V> parent, V source, V sink)

| Linha | Código                                     | Complexidade Temporal | Justificação                                     |
|-------|--------------------------------------------|-----------------------|--------------------------------------------------|
| 107   | `LinkedList<V> path = new LinkedList<>();` | **O(1)**              | Criação da lista ligada                          |
| 108   | `V current = sink;`                        | **O(1)**              | Atribuição                                       |
| 110   | `while (!current.equals(source))`          | **O(n)**              | Itera até n vértices no caminho                  |
| 111   | `path.addFirst(current);`                  | **O(1)**              | Inserção no início de LinkedList                 |
| 112   | `current = parent.get(current);`           | **O(1)**              | Acesso ao mapa                                   |
| 114   | `path.addFirst(source);`                   | **O(1)**              | Inserção no início                               |
| 115   | `return path;`                             | **O(1)**              | Retorno                                          |

### Complexidade Temporal Total:
**O(n)**


---

### findMinCapacity(List<V> path)

| Linha | Código                                                                 | Complexidade Temporal | Justificação                        |
|-------|------------------------------------------------------------------------|-----------------------|-------------------------------------|
| 119   | `int minCapacity = Integer.MAX_VALUE;`                                 | **O(1)**              | Inicialização                       |
| 121   | `for (int i = 0; i < path.size() - 1; i++)`                            | **O(n)**              | Itera sobre arestas do caminho      |
| 122   | `V u = path.get(i);`                                                   | **O(1)**              | Acesso à lista                      |
| 123   | `V v = path.get(i + 1);`                                               | **O(1)**              | Acesso à lista                      |
| 124   | `minCapacity = Math.min(minCapacity, residualCapacity.get(u).get(v));` | **O(1)**              | Acesso ao mapa e comparação         |
| 126   | `return minCapacity;`                                                  | **O(1)**              | Retorno                             |

### Complexidade Temporal Total:
**O(n)**

---

### updateResidualCapacity(List<V> path, int flow)

| Linha | Código                                                                               | Complexidade Temporal | Justificação                    |
|-------|--------------------------------------------------------------------------------------|-----------------------|---------------------------------|
| 130   | `for (int i = 0; i < path.size() - 1; i++)`                                          | **O(n)**              | Itera sobre arestas do caminho  |
| 131   | `V u = path.get(i);`                                                                 | **O(1)**              | Acesso à lista                  |
| 132   | `V v = path.get(i + 1);`                                                             | **O(1)**              | Acesso à lista                  |
| 134   | `residualCapacity.get(u).put(v, residualCapacity.get(u).get(v) - flow);`             | **O(1)**              | Atualização no mapa             |
| 135   | `residualCapacity.get(v).put(u, residualCapacity.get(v).getOrDefault(u, 0) + flow);` | **O(1)**              | Atualização da aresta reversa   |

### Complexidade Temporal Total:
**O(n)**

---

## Considerações Finais

**Complexidade Total do Algoritmo (Edmonds-Karp):**

**O(n·m²)**

### Justificação:
- **BFS encontra o caminho mais curto:** O(n+m) por iteração
- **Número máximo de iterações:** O(n·m) - cada iteração satura pelo menos uma aresta e o comprimento dos caminhos aumentantes nunca diminui
- **Total:** O(n·m·(n+m)) = **O(n·m²)** para grafos densos (onde m ≈ n²)

### Vantagem da BFS (Edmonds-Karp) sobre DFS (Ford-Fulkerson):
O uso de **BFS** garante complexidade polinomial **O(n·m²)**, independentemente das capacidades das arestas. Por outro lado, a versão com **DFS** pode resultar em **O(f·(n+m))**, onde **f** (valor do fluxo máximo) pode ser exponencialmente grande em relação ao tamanho da entrada.

### Impacto Prático:
- **Edmonds-Karp (BFS):** Sempre eficiente para redes de transporte reais
- **Ford-Fulkerson (DFS):** Pode ter desempenho muito pior em grafos com capacidades inteiras grandes, potencialmente executando milhões de iterações desnecessárias