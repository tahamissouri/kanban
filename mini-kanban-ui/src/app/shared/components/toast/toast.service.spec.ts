import { ToastService } from './toast.service';

describe('ToastService', () => {
  let service: ToastService;

  beforeEach(() => {
    service = new ToastService();
    jest.useFakeTimers();
  });

  afterEach(() => jest.useRealTimers());

  it('should start with no toasts', () => {
    expect(service.toasts()).toEqual([]);
  });

  it('show() should add a toast with the given type', () => {
    service.show('Hello', 'info');
    expect(service.toasts().length).toBe(1);
    expect(service.toasts()[0].message).toBe('Hello');
    expect(service.toasts()[0].type).toBe('info');
  });

  it('success() should add a success toast', () => {
    service.success('Saved!');
    expect(service.toasts()[0].type).toBe('success');
  });

  it('error() should add an error toast', () => {
    service.error('Something broke');
    expect(service.toasts()[0].type).toBe('error');
  });

  it('dismiss() should remove the toast by id', () => {
    service.show('Test', 'info');
    const id = service.toasts()[0].id;
    service.dismiss(id);
    expect(service.toasts()).toEqual([]);
  });

  it('should auto-dismiss after duration', () => {
    service.show('Auto', 'info', 3500);
    expect(service.toasts().length).toBe(1);
    jest.advanceTimersByTime(3500);
    expect(service.toasts().length).toBe(0);
  });

  it('should support multiple simultaneous toasts', () => {
    service.show('A', 'success');
    service.show('B', 'error');
    service.show('C', 'info');
    expect(service.toasts().length).toBe(3);
  });

  it('each toast should have a unique id', () => {
    service.show('A', 'info');
    service.show('B', 'info');
    const ids = service.toasts().map(t => t.id);
    expect(new Set(ids).size).toBe(2);
  });

  it('dismissing a non-existent id should not throw', () => {
    service.show('A', 'info');
    expect(() => service.dismiss(9999)).not.toThrow();
    expect(service.toasts().length).toBe(1);
  });
});
