import { TransactionHistory } from "./components/TransactionHistory/TransactionHistory";
import { TransferForm } from "./components/TransferForm/TransferForm";
import "./App.css";

function App() {
  return (
    <main className="app">
      <header className="app__header">
        <p className="app__eyebrow">Transferencias App</p>
        <h1>Gestiona tu dinero fácilmente</h1>
        <p className="app__subtitle">
          Envía fondos de forma rápida y consulta tus movimientos en un solo lugar.
        </p>
      </header>
      <div className="app__content">
        <TransferForm />
        <TransactionHistory />
      </div>
    </main>
  );
}

export default App;
