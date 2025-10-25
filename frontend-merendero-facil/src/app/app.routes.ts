import { Routes } from '@angular/router';
import { CreateUserComponent } from './user/components/create-user/create-user.component';
import { LoginUserComponent } from './user/components/login-user/login-user.component';
import { SaveMerenderoComponent } from './merendero/components/save-merendero/save-merendero.component';
import { ListMerenderosComponent } from './merendero/components/list-merenderos/list-merenderos.component';
import { ForgotPasswordComponent } from './user/components/forgot-password/forgot-password.component';
import { ListUsersComponent } from './user/components/list-users/list-users.component';
import { TerminosComponent } from './shared/components/terminos/terminos.component';
import { PreguntasComponent } from './shared/components/preguntas/preguntas.component';
import { DonateComponent } from './donation/components/donate/donate.component';
import { DonationsComponent } from './donation/components/list-donations/donations.component';
import { MerenderoOkComponent } from './merendero/components/merendero-ok/merendero-ok.component';
import { MerenderoFailureComponent } from './merendero/components/merendero-failure/merendero-failure.component';
import { InsumosComponent } from './stock/components/supply/insumos/insumos.component';
import { AddSupplyComponent } from './stock/components/supply/add-supply/add-supply.component';
import { EntradasComponent } from './stock/components/movement/entradas/entradas.component';
import { SalidasComponent } from './stock/components/movement/salidas/salidas.component';
import { AddEntradaComponent } from './stock/components/movement/add-entrada/add-entrada.component';
import { AddSalidaComponent } from './stock/components/movement/add-salida/add-salida.component';
import { InventarioComponent } from './stock/components/stock/inventario/inventario.component';
import { DonationDashboardComponent } from './donation/components/donation-dashboard/donation-dashboard.component';
import { GastosComponent } from './expense/components/gastos/gastos.component';
import { AddGastoComponent } from './expense/components/add-gasto/add-gasto.component';
import { ExpenseDashboardComponent } from './expense/components/expense-dashboard/expense-dashboard.component';
import { MovementsDashboardComponent } from './stock/components/movement/movements-dashboard/movements-dashboard.component';

export const routes: Routes = [
    { path: '', redirectTo: '/list-merenderos', pathMatch: 'full' },
    { path: 'create-user', component: CreateUserComponent },
    { path: 'login-user', component: LoginUserComponent },
    { path: 'forgot-password', component: ForgotPasswordComponent },
    { path: 'list-users', component: ListUsersComponent },
    { path: 'terminos', component: TerminosComponent },
    { path: 'preguntas', component: PreguntasComponent },

    { path: 'create-merendero', component: SaveMerenderoComponent },
    { path: 'list-merenderos', component: ListMerenderosComponent },
    { path: 'merendero-ok', component: MerenderoOkComponent },
    { path: 'failure', component: MerenderoFailureComponent },

    { path: 'donate/:id', component: DonateComponent },
    { path: 'donaciones', component: DonationsComponent },


    { path: 'stock/insumos', component: InsumosComponent },
    { path: 'stock/add-supply', component: AddSupplyComponent },
    { path: 'stock/inventario', component: InventarioComponent },

    { path: 'stock/movimientos/entradas', component: EntradasComponent },
    { path: 'stock/movimientos/salidas', component: SalidasComponent },
    { path: 'stock/add-entry', component: AddEntradaComponent },
    { path: 'stock/add-output', component: AddSalidaComponent },

    { path: 'stock/movimientos/gastos', component: GastosComponent },
    { path: 'stock/add-expense', component: AddGastoComponent },
    
    { path: 'donaciones/reportes', component: DonationDashboardComponent },
    { path: 'stock/reporte-1', component: MovementsDashboardComponent },
    { path: 'stock/reporte-2', component: ExpenseDashboardComponent }
];